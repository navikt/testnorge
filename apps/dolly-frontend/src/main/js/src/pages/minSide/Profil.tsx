// @ts-ignore
import dolly from '@/favicon.ico'
import playwright from '@/assets/img/playwright.png'
import { useBrukerProfil, useBrukerProfilBilde } from '@/utils/hooks/useBruker'
import { runningE2ETest } from '@/service/services/Request'
import { formatBrukerNavn } from '@/utils/DataFormatter'

export const getDefaultImage = () => {
	return runningE2ETest() ? playwright : dolly
}

export default function Profil() {
	const { brukerProfil: info } = useBrukerProfil()
	const { brukerBilde: bilde } = useBrukerProfilBilde()

	const brukerNavn = formatBrukerNavn(info?.visningsNavn)

	const bankIdProfil = info && info.type && info.type === 'BankId'

	return (
		<div className="profil">
			<img alt="Profilbilde" src={bilde || getDefaultImage()} />
			{info && !bankIdProfil && (
				<div className="person-info">
					<p>
						<span className="blokk">NAVN</span>
						<span>{brukerNavn}</span>
					</p>
					<p>
						<span className="blokk">E-POST</span>
						<span>{info.epost}</span>
					</p>
					<p>
						<span className="blokk">AVDELING</span>
						<span>{info.avdeling}</span>
					</p>
				</div>
			)}
			{info && bankIdProfil && (
				<div className="person-info">
					<p>
						<span className="blokk">BRUKERNAVN</span>
						<span>{info.visningsNavn}</span>
					</p>
					<p>
						<span className="blokk">ORGANISASJON</span>
						<span>{info.organisasjon}</span>
					</p>
				</div>
			)}
		</div>
	)
}
