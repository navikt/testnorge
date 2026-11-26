import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { PencilIcon, XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { miljoer } from '@/pages/adminPages/Orgtilgang/OrgtilgangForm'
import { Button, Checkbox, CheckboxGroup, Dialog, InfoCard } from '@navikt/ds-react'

type RedigerTypes = {
	orgNr: string
	miljoe: string
	mutate: Function
}

type RedigerValues = {
	miljoe: string[]
	organisasjonsnummer: string
}

export const RedigerOrganisasjon = ({ orgNr, miljoe, mutate }: RedigerTypes) => {
	const [isLoading, setIsLoading, resetLoading] = useBoolean(false)
	const [dialogIsOpen, setDialogIsOpen] = useState(false)
	const [error, setError] = useState<Error | null>(null)
	const formId = `rediger-organisasjon-${orgNr}`

	const { handleSubmit, control, watch } = useForm<RedigerValues>({
		mode: 'onChange',
		defaultValues: {
			miljoe: miljoe?.includes(',') ? miljoe.split(',') : [miljoe],
			organisasjonsnummer: orgNr,
		},
	})

	const valgteMiljoer = watch('miljoe')

	const handleOpenChange = (open: boolean) => {
		if (open) {
			resetLoading()
			setError(null)
		}
		setDialogIsOpen(open)
	}

	const updateOrganisasjon = async (values: RedigerValues) => {
		setIsLoading(true)
		await OrganisasjonTilgangService.updateOrganisasjon(values)
			.then(() => {
				mutate().then(() => {
					resetLoading()
					setDialogIsOpen(false)
				})
			})
			.catch((error) => {
				setError(error)
				resetLoading()
			})
	}

	return (
		<Dialog open={dialogIsOpen} onOpenChange={handleOpenChange}>
			<Dialog.Trigger>
				<Button
					variant="tertiary"
					icon={<PencilIcon title="Endre tilgang" />}
					size="small"
					style={{ marginLeft: '10px' }}
				/>
			</Dialog.Trigger>
			<Dialog.Popup>
				<Dialog.Header>
					<Dialog.Title>Oppdatere organisasjonstilgang</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<form id={formId} autoComplete="off" onSubmit={handleSubmit(updateOrganisasjon)}>
						<Controller
							name="miljoe"
							control={control}
							render={({ field }) => (
								<CheckboxGroup
									legend="Miljø"
									className="checkbox-group--aksel-default"
									value={field.value}
									onChange={field.onChange}
									error={field.value?.length === 0 && 'Velg minst ett miljø'}
								>
									{miljoer.map((miljoeOption) => (
										<Checkbox key={miljoeOption.value} value={miljoeOption.value}>
											{miljoeOption.label}
										</Checkbox>
									))}
								</CheckboxGroup>
							)}
						/>
					</form>
					{error && (
						<InfoCard data-color="danger" size="small" style={{ marginTop: '1rem' }}>
							<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
								Feil ved oppdatering: {error.message}
							</InfoCard.Message>
						</InfoCard>
					)}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary">Avbryt</Button>
					</Dialog.CloseTrigger>
					<Button
						type="submit"
						form={formId}
						loading={isLoading}
						disabled={valgteMiljoer?.length === 0}
					>
						Endre tilgang
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
