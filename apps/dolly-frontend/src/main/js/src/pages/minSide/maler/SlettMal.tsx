import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { TrashIcon } from '@navikt/aksel-icons'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import React from 'react'
import { DollyApi } from '@/service/Api'
import useBoolean from '@/utils/hooks/useBoolean'
import { MalType } from '@/pages/minSide/maler/Maloversikt'
import { tenorSlettPersonMal } from '@/service/services/templatesearch/TemplateSearch'

export const SlettMal = ({ id, type, mutate }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	const slettMal = () => {
		switch (type) {
			case MalType.ORGANISASJON:
				return DollyApi.slettMalOrganisasjon(id).then(() => mutate())
			case MalType.PERSON:
				return DollyApi.slettMal(id).then(() => mutate())
			case MalType.TENORSOEK:
				return tenorSlettPersonMal(id).then(() => mutate())
			default:
				return null
		}
	}

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_MALER_SLETT}
				onClick={openModal}
				variant={'tertiary'}
				icon={<TrashIcon title="Slett" />}
				size={'small'}
			/>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'40%'} overflow={'auto'}>
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Slett mal</h1>
						<h4>Er du sikker på at du vil slette denne malen?</h4>
					</div>
					<div className="slettModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_MALER_SLETT_BEKREFT}
							onClick={() => {
								closeModal()
								slettMal()
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
