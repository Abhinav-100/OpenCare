import fs from "fs";
import path, { dirname } from "path";
import { fileURLToPath } from "url";
import { FlatCompat } from "@eslint/eslintrc";
import importPlugin from "eslint-plugin-import";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const modulesRoot = path.join(__dirname, "src", "modules");
const moduleNames = fs.existsSync(modulesRoot)
  ? fs
      .readdirSync(modulesRoot, { withFileTypes: true })
      .filter((entry) => entry.isDirectory())
      .map((entry) => entry.name)
      .sort()
  : [];

const moduleBoundaryConfigs = moduleNames.map((moduleName) => {
  const restrictedPatterns = moduleNames
    .filter((otherModule) => otherModule !== moduleName && otherModule !== "platform")
    .flatMap((otherModule) => [
      {
        group: [
          `@/modules/${otherModule}/components/**`,
          `@/modules/${otherModule}/hooks/**`,
          `@/modules/${otherModule}/types/**`,
        ],
        message:
          "Cross-module UI/hooks/types imports are not allowed. Use module public APIs or shared layer.",
      },
    ]);

  return {
    files: [`src/modules/${moduleName}/**/*.{ts,tsx}`],
    rules: {
      "no-restricted-imports": ["error", { patterns: restrictedPatterns }],
    },
  };
});

const eslintConfig = [
  ...compat.extends("next/core-web-vitals", "next/typescript"),
  {
    files: ["src/**/*.{ts,tsx}"],
    plugins: {
      import: importPlugin,
    },
    rules: {
      "no-restricted-imports": [
        "error",
        {
          paths: [
            {
              name: "@/components",
              message: "Use domain modules or shared layer, not flat components.",
            },
            {
              name: "@/hooks",
              message: "Use domain modules or shared layer, not flat hooks.",
            },
            {
              name: "@/api",
              message: "Use module-local API clients under src/modules/<domain>/api.",
            },
            {
              name: "@/services",
              message: "Use module-local services or shared utilities.",
            },
          ],
          patterns: [
            {
              group: [
                "@/components/**",
                "@/hooks/**",
                "@/api/**",
                "@/services/**",
              ],
              message:
                "Flat-layer imports are forbidden. Import from modules/* or shared/* only.",
            },
            {
              group: [
                "../../../../../components/**",
                "../../../../components/**",
                "../../../components/**",
                "../../components/**",
                "../components/**",
              ],
              message:
                "Use absolute module/shared imports instead of deep relative component paths.",
            },
          ],
        },
      ],
      "import/order": [
        "warn",
        {
          groups: ["builtin", "external", "internal", ["parent", "sibling", "index"], "type"],
          "newlines-between": "ignore",
          alphabetize: {
            order: "ignore",
            caseInsensitive: true,
          },
        },
      ],
    },
  },
  ...moduleBoundaryConfigs,
];

export default eslintConfig;
