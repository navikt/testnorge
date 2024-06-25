import { Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { TrashIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React from 'react'
import DollyModal from '@/components/ui/modal/DollyModal'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'

type DeleteTypes = {
	orgNr: string
	navn: string
	mutate: Function
}

export const DeleteOrganisasjon = ({ orgNr, navn, mutate }: DeleteTypes) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	const slettOrg = () => {
		OrganisasjonTilgangService.deleteOrganisasjoner(orgNr).then((response) => {
			mutate()
		})
	}

	return (
		<>
			<Button onClick={openModal} variant={'tertiary'} icon={<TrashIcon />} size={'small'} />
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'40%'} overflow={'auto'}>
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Fjern tilgang</h1>
						<h4>Er du sikker på at du vil fjerne tilgang til Dolly for {navn}?</h4>
					</div>
					<div className="slettModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								slettOrg()
							}}
							variant={'primary'}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</>
	)
}
