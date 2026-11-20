import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { TrashIcon, XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { Button, InfoCard } from '@navikt/ds-react'

type DeleteTypes = {
	orgNr: string
	navn: string
	mutate: Function
}

export const DeleteOrganisasjon = ({ orgNr, navn, mutate }: DeleteTypes) => {
	const [isLoading, setIsLoading, resetLoading] = useBoolean(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [error, setError] = useState(null)

	const slettOrg = async () => {
		setIsLoading(true)
		await OrganisasjonTilgangService.deleteOrganisasjoner(orgNr)
			.then(() => {
				mutate().then(() => {
					resetLoading()
					closeModal()
				})
			})
			.catch((error) => {
				setError(error)
				resetLoading()
			})
	}

	return (
		<>
			<Button
				onClick={openModal}
				variant={'tertiary'}
				icon={<TrashIcon title={'Slett tilgang'} />}
				size={'small'}
			/>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'40%'} overflow={'auto'}>
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Fjern tilgang</h1>
						<h4>Er du sikker på at du vil fjerne tilgang til Dolly for {navn}?</h4>
					</div>
					{error && (
						<InfoCard data-color="danger" size={'small'}>
							<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
								Feil ved sletting: {error.message}
							</InfoCard.Message>
						</InfoCard>
					)}
					<div className="slettModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							onClick={() => {
								slettOrg()
							}}
							variant={'primary'}
							loading={isLoading}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</>
	)
}
