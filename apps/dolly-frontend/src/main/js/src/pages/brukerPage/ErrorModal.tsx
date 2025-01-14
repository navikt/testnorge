import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import React from 'react'
import logoutBruker from '@/components/utlogging/logoutBruker'

export const ErrorModal = ({ errorModalIsOpen, closeErrorModal, error }) => {
	return (
		<DollyModal
			isOpen={errorModalIsOpen}
			closeModal={() => {
				closeErrorModal()
				logoutBruker(error)
			}}
			width="40%"
			overflow="auto"
		>
			<div className="slettModal">
				<div className="slettModal slettModal-content">
					<Icon size={50} kind="report-problem-circle" />
					<h1>Manglende tilgang</h1>
					<h4>
						Du er nå pålogget med Bank-ID, men mangler tilgang til Dolly. <br/>
						For å få tilgang må følgende være oppfylt:
						<ol>
							<li>Organisasjonen du arbeider for må være lagt til i
								liste over godkjente organisasjoner hos Nav.
							</li>
							<li>Du trenger rettigheten <em>"Tilgang til NAVs Dolly
								for samarbeidspartnere"</em> i Altinn for denne organisasjonen.
							</li>
						</ol>
						Hvis hensikten er å ta i bruk Dolly kan dette
						løses som følger:
						<ol>
							<li>Ta kontakt med NAV på epost: dolly@nav.no,
								oppgi organisasjonsnummer til organisasjon du jobber for og spør om denne kan gis tilgang
								til <em>Dolly syntetiske testdata selvbetjening</em>.
							</li>
							<li>Ta kontakt med Altinn-ansvarlig i organisasjonen du jobber for
								og spør om vedkommende kan gi deg rettighet til enkelttjenesten med navn:
								<em>Tilgang til NAVs Dolly for samarbeidspartnere</em>.
							</li>
						</ol>
						Hvis overnevnte er på plass og du forsatt mangler tilgang, ta kontakt med NAV ved anders.marstrander@nav.no.
					</h4>
				</div>
				<div className="slettModal-actions">
					<NavButton
						onClick={() => {
							closeErrorModal()
							logoutBruker(error)
						}}
						variant="primary"
					>
						OK
					</NavButton>
				</div>
			</div>
		</DollyModal>
	)
}
