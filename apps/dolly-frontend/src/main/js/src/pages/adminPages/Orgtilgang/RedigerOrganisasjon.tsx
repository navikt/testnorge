import { Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { PencilIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormProvider, useForm } from 'react-hook-form'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import './RedigerModal.less'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { useSWRConfig } from 'swr'

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
	const { mutate: schmutate } = useSWRConfig()
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
		schmutate('/testnav-organisasjon-tilgang-service/api/v1/organisasjoner', (currentData) =>
			currentData.map((org) => {
				if (org.organisasjonsnummer === values.organisasjonsnummer) {
					return {
						...org,
						gyldigTil: values.gyldigTil,
						miljoe: values.miljoe.join(','),
					}
				}
				return org
			}),
		)
		closeModal()
	}

	return (
		<FormProvider {...formMethods}>
			<Button
				onClick={openModal}
				variant={'tertiary'}
				icon={<PencilIcon title={'Endre tilgang'} />}
				size={'small'}
				style={{ marginLeft: '10px' }}
			/>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'50%'} overflow={'visible'}>
				<div className="redigerModal">
					<div className="redigerModal redigerModal-content">
						<h1>Endre utløpsdato og miljøer</h1>
					</div>
					<div className="redigerModal redigerModal-input">
						<FormDatepicker name={'gyldigTil'} label="Ny utløpsdato" />
						<FormSelect
							name={'miljoe'}
							label={'Miljø'}
							options={miljoeOptions}
							isClearable={false}
							isMulti={true}
							size={'grow	'}
						/>
					</div>
					<ModalActionKnapper
						submitknapp="Endre tilgang"
						disabled={values.miljoe.length === 0 || !values.gyldigTil}
						onSubmit={() => updateOrganisasjon()}
						onAvbryt={closeModal}
						center
					/>
				</div>
			</DollyModal>
		</FormProvider>
	)
}
