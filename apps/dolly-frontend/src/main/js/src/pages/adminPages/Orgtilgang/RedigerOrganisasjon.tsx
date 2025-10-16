import { Alert, Button } from '@navikt/ds-react'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { PencilIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Form, FormProvider, useForm } from 'react-hook-form'
import './RedigerModal.less'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { miljoer } from '@/pages/adminPages/Orgtilgang/OrgtilgangForm'

type RedigerTypes = {
	orgNr: string
	miljoe: string
	mutate: Function
}

export const RedigerOrganisasjon = ({ orgNr, miljoe, mutate }: RedigerTypes) => {
	const [isLoading, setIsLoading, resetLoading] = useBoolean(false)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [error, setError] = useState(null)

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: {
			miljoe: miljoe?.includes(',') ? miljoe.split(',') : [miljoe],
			organisasjonsnummer: orgNr,
		},
	})

	const { handleSubmit, control, watch } = formMethods
	const values = watch()

	const updateOrganisasjon = async () => {
		setIsLoading(true)
		await OrganisasjonTilgangService.updateOrganisasjon(values)
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
		<FormProvider {...formMethods}>
			<Button
				onClick={() => {
					resetLoading()
					setError(null)
					openModal()
				}}
				variant={'tertiary'}
				icon={<PencilIcon title={'Endre tilgang'} />}
				size={'small'}
				style={{ marginLeft: '10px' }}
			/>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width={'50%'} overflow={'visible'}>
				<Form control={control} autoComplete="off" onSubmit={handleSubmit(updateOrganisasjon)}>
					<div className="redigerModal">
						<div className="redigerModal redigerModal-content">
							<h1>Oppdatere organisasjonstilgang</h1>
						</div>
						<div className="redigerModal redigerModal-input">
							<FormSelect
								name={'miljoe'}
								label={'Miljø'}
								options={miljoer}
								isClearable={false}
								isMulti={true}
								size={'grow'}
							/>
						</div>
						{error && (
							<Alert variant={'error'} size={'small'}>
								Feil ved oppdatering: {error.message}
							</Alert>
						)}
						<ModalActionKnapper
							submitknapp="Endre tilgang"
							disabled={values.miljoe.length === 0}
							onAvbryt={closeModal}
							loading={isLoading}
							center
						/>
					</div>
				</Form>
			</DollyModal>
		</FormProvider>
	)
}
