import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Icon from '~/components/ui/icon/Icon'

type Props = {
	brukernavn: string
	onDelete: () => void
}

export default function SlettBruker({ brukernavn, onDelete }: Props) {
	const [isSlettModalOpen, openSlettModal, closeSlettModal] = useBoolean(false)

	return (
		<div className="slettbruker">
			<hr />
			<div className="flexbox--space">
				<div>
					<h2>Sletting av bruker</h2>
					<p>
						Om du ønsker å slette brukeren <b>{brukernavn}</b> kan du gjøre det her. Vær oppmerksom
						på at du da også vil slette alle grupper og tilhørende personer, organisasjoner og
						maler.
					</p>
				</div>
				<div className="flexbox">
					<NavButton type="hoved" onClick={openSlettModal} className="slettbruker__button">
						Slett bruker
					</NavButton>
				</div>
				<DollyModal
					isOpen={isSlettModalOpen}
					closeModal={closeSlettModal}
					width="35%"
					overflow="auto"
				>
					<div className="slettbruker__modal">
						<Icon kind="report-problem-circle" size={60} />
						<h1>Sletting av bruker</h1>
						<h3>Er du sikker på at du vil slette denne brukeren og alle tilhørende data?</h3>
						<div className="flexbox--align-center--justify-center">
							<NavButton className="nei-button" type="hoved" onClick={closeSlettModal}>
								Nei
							</NavButton>
							<NavButton className="ja-button" type="standard" onClick={onDelete}>
								Ja, jeg er sikker
							</NavButton>
						</div>
					</div>
				</DollyModal>
			</div>
		</div>
	)
}
