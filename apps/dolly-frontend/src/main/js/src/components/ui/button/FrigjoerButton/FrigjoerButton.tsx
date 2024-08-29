import NavButton from '@/components/ui/button/NavButton/NavButton'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Button from '@/components/ui/button/Button'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'

import './FrigjoerModal.less'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import React from 'react'

type Props = {
	slettPerson: Function
	loading: boolean
	disabled?: boolean
}

export const FrigjoerButton = ({ slettPerson, loading, disabled = false }: Props) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const mutate = useMatchMutate()

	if (loading) {
		return <Loading label="frigjør..." />
	}

	const infoTekst = () => {
		return (
			'Er du sikker på at du vil frigjøre denne personen? All ekstra informasjon lagt til på ' +
			'personen via Dolly vil bli slettet og personen vil bli frigjort fra gruppen.'
		)
	}

	return (
		<React.Fragment>
			<Button
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Frigjøring/sletting er midlertidig utilgjengelig' : ''}
				kind="trashcan"
			>
				FRIGJØR/SLETT
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="frigjoerModal">
					<div className="frigjoerModal frigjoerModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Frigjør/slett</h1>
						<h4>{infoTekst()}</h4>
					</div>
					<div className="frigjoerModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								slettPerson().then(() => mutate(REGEX_BACKEND_GRUPPER))
							}}
							variant={'primary'}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
