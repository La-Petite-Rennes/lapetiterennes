package fr.lpr.membership.domain.sale;

public enum PaymentType {

	Cash,
	Check,
	Galleco,
    Online,
    CreditCard,
    CrowdFunding,
	Waiting,
	NotSpecified // Used only to map old membership
}
