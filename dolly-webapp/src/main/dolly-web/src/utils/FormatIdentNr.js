// Skriv ut FNR og DNR med mellom mellom fødselsdato og personnummer
// Ex: 010195 12345
export default function FormatIdentNr(ident) {
	const birth = ident.substring(0, 6)
	const personnummer = ident.substring(6, 11)
	return `${birth} ${personnummer}`
}
