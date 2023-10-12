// @ts-ignore
import dolly from '@/favicon.ico'
import cypress from '@/assets/img/cypress.png'
import { useBrukerProfil, useBrukerProfilBilde } from '@/utils/hooks/useBruker'
import { runningCypressE2E } from '@/service/services/Request'

export const getDefaultImage = () => {
	return runningCypressE2E() ? cypress : dolly
}

export default function Profil() {
	const { brukerProfil: info } = useBrukerProfil()
	const { brukerBilde: bilde } = useBrukerProfilBilde()

	const bankIdProfil = info && info.type && info.type === 'BankId'

	return (
		<div className="profil">
			<img alt="Profilbilde" src={bilde || getDefaultImage()} />
			{info && !bankIdProfil && (
				<div className="person-info">
					<p>
						<span className="blokk">NAVN</span>
						<span>{info.visningsNavn}</span>
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
