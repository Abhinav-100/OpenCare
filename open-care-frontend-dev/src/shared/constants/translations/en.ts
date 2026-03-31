// English translations
export const en = {
  // Common
  common: {
    search: "Search",
    loading: "Loading...",
    viewAll: "View All",
    learnMore: "Learn More",
    bookNow: "Book Now",
    cancel: "Cancel",
    save: "Save",
    confirm: "Confirm",
    back: "Back",
    next: "Next",
    submit: "Submit",
    close: "Close",
  },

  // Navigation
  nav: {
    home: "Home",
    doctors: "Doctors",
    hospitals: "Hospitals",
    bloodBanks: "Blood Banks",
    healthSchemes: "Health Schemes",
    appointments: "My Appointments",
    healthRecords: "Health Records",
    dashboard: "Dashboard",
    login: "Login",
    logout: "Sign Out",
    profile: "Profile",
  },

  // Hero Section
  hero: {
    title: "Your Health, Our Priority",
    subtitle: "Find the best doctors and hospitals in Odisha",
    searchPlaceholder: "Search doctors, hospitals, specialties...",
    stats: {
      doctors: "Doctors",
      hospitals: "Hospitals",
      patients: "Happy Patients",
    },
  },

  // Doctors
  doctors: {
    title: "Find Doctors",
    subtitle: "Search from our network of qualified healthcare professionals",
    filters: {
      specialization: "Specialization",
      district: "District",
      gender: "Gender",
      experience: "Experience",
      allDistricts: "All Districts",
      allSpecializations: "All Specializations",
    },
    card: {
      experience: "years experience",
      consultationFee: "Consultation Fee",
      online: "Online",
      inPerson: "In-Person",
      viewProfile: "View Profile",
      bookAppointment: "Book Appointment",
    },
    noResults: "No doctors found matching your criteria",
  },

  // Hospitals
  hospitals: {
    title: "Find Hospitals",
    subtitle: "Locate healthcare facilities near you",
    filters: {
      district: "District",
      type: "Hospital Type",
      services: "Services",
    },
    card: {
      beds: "Beds",
      emergency: "Emergency",
      available: "Available",
      viewDetails: "View Details",
    },
    noResults: "No hospitals found matching your criteria",
  },

  // Appointments
  appointments: {
    title: "My Appointments",
    upcoming: "Upcoming",
    past: "Past",
    bookNew: "Book New Appointment",
    status: {
      confirmed: "Confirmed",
      pending: "Pending",
      completed: "Completed",
      cancelled: "Cancelled",
    },
    noAppointments: "No appointments found",
    bookFirst: "Book your first appointment",
  },

  // Health Records
  healthRecords: {
    title: "My Health Records",
    subtitle: "Track and manage your medical history",
    tabs: {
      visits: "Visits",
      conditions: "Conditions",
      medications: "Medications",
      vitals: "Vitals",
    },
    addVisit: "Add Visit",
    addCondition: "Add Condition",
    addMedication: "Add Medication",
    noRecords: "No records found",
  },

  // Health Schemes
  healthSchemes: {
    title: "Government Health Schemes",
    subtitle: "Healthcare benefits for Odisha residents",
    bsky: {
      name: "BSKY (Biju Swasthya Kalyan Yojana)",
      description: "Free healthcare for Odisha families",
      coverage: "Up to ₹5 Lakh per family per year",
    },
    ayushman: {
      name: "Ayushman Bharat - PMJAY",
      description: "National health protection scheme",
      coverage: "Up to ₹5 Lakh per family per year",
    },
    checkEligibility: "Check Eligibility",
    benefits: "Benefits",
    howToApply: "How to Apply",
  },

  // Dashboard
  dashboard: {
    welcome: "Welcome back",
    quickActions: "Quick Actions",
    findDoctors: "Find Doctors",
    findHospitals: "Find Hospitals",
    myAppointments: "My Appointments",
    myProfile: "My Profile",
    healthRecords: "Health Records",
    quickStats: "Quick Stats",
    totalAppointments: "Total Appointments",
    activeMedications: "Active Medications",
  },

  // Footer
  footer: {
    about: "About OpenCare",
    aboutText: "OpenCare is Odisha's premier healthcare platform connecting patients with doctors and hospitals.",
    quickLinks: "Quick Links",
    contact: "Contact Us",
    address: "Bhubaneswar, Odisha, India",
    copyright: "© 2024 OpenCare. All rights reserved.",
  },

  // Language
  language: {
    english: "English",
    odia: "Odia",
    switchTo: "Switch to",
  },
};

export type Translations = typeof en;
