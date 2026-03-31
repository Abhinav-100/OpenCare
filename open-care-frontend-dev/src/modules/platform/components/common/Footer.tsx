export default function Footer() {
	return (
		<footer className="bg-gray-800 text-white py-8">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="flex flex-col md:flex-row justify-between items-center gap-4">
					{/* Brand */}
					<div className="text-center md:text-left">
						<h3 className="text-xl font-bold text-teal-400">OpenCare</h3>
						<p className="text-gray-400 text-sm">
							Healthcare Platform for Odisha
						</p>
					</div>

					{/* Contact */}
					<div className="text-center text-gray-400 text-sm">
						<p>support@opencare.in</p>
						<p>Bhubaneswar, Odisha, India</p>
					</div>
				</div>

				{/* Copyright */}
				<div className="border-t border-gray-700 mt-6 pt-4 text-center text-gray-500 text-sm">
					© 2025 OpenCare — A College Project
				</div>
			</div>
		</footer>
	);
}
