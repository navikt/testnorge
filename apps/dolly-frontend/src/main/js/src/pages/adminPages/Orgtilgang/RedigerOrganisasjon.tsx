import { Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { PencilIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React from 'react'
import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { formatDate } from '@/utils/DataFormatter'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormProvider, useForm } from 'react-hook-form'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import './RedigerModal.less'
import { DollyModal } from '@/components/ui/modal/DollyModal'

type RedigerTypes = {
	orgNr: string
	gyldigTil: string
	miljoe: string
	mutate: Function
}

const miljoeOptions = [
	{ label: 'Q1', value: 'q1' },
	{ label: 'Q2', value: 'q2' },
]

export const RedigerOrganisasjon = ({ orgNr, gyldigTil, miljoe, mutate }: RedigerTypes) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: {
			miljoe: miljoe?.includes(',') ? miljoe.split(',') : [miljoe],
			organisasjonsnummer: orgNr,
			gyldigTil,
		},
	})

	const values = formMethods.watch()

	const updateOrganisasjon = () => {
		OrganisasjonTilgangService.updateOrganisasjon(values).then(() => {
			mutate()
		})
	}

	return (
		<FormProvider {...formMethods}>
			<Button
				onClick={openModal}
				variant={'tertiary'}
				icon={<PencilIcon />}
				size={'small'}
				style={{ marginLeft: '10px' }}
			/>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'50%'} overflow={'visible'}>
				<div className="redigerModal">
					<div className="redigerModal redigerModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Endre utløpsdato</h1>
						<h4>
							Er du sikker på at du vil endre utløpsdato til {formatDate(values.gyldigTil)} og
							tillate miljø: {values.miljoe?.join(',')}?
						</h4>
						<div className="redigerModal redigerModal-input">
							<FormDatepicker name={'gyldigTil'} label="Ny utløpsdato" />
							<FormSelect
								name={'miljoe'}
								label={'Miljø'}
								options={miljoeOptions}
								isClearable={false}
								isMulti={true}
							/>
						</div>
					</div>
					<div className="redigerModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							onClick={() => {
								updateOrganisasjon()
								closeModal()
							}}
							variant={'primary'}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</FormProvider>
	)
}
