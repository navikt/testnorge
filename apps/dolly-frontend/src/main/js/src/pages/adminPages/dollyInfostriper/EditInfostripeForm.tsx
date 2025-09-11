import React, { useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { Alert, Box, Button, Textarea } from '@navikt/ds-react'
import { InfoStripeType, useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { InfostripeFormValues } from '@/pages/adminPages/dollyInfostriper/CreateInfostripeForm'

interface Props {
	stripe: InfoStripeType
	onDone(): void
}

const infostripeTypeOptions = [
	{ label: 'Info', value: 'INFO' },
	{ label: 'Advarsel', value: 'WARNING' },
	{ label: 'Feil', value: 'ERROR' },
	{ label: 'Suksess', value: 'SUCCESS' },
]

export const EditInfostripeForm: React.FC<Props> = ({ stripe, onDone }) => {
	const { updateInfostripe } = useDollyInfostriper()
	const [serverError, setServerError] = useState<string | null>(null)

	const methods = useForm<InfostripeFormValues>({
		defaultValues: {
			type: stripe.type,
			message: stripe.message,
			start: stripe.start,
			expires: stripe.expires,
		},
	})

	const onSubmit = async (data: InfostripeFormValues) => {
		setServerError(null)
		try {
			await updateInfostripe({
				id: stripe.id,
				type: data.type,
				message: data.message.trim(),
				start: data.start,
				expires: data.expires,
			})
			onDone()
		} catch (e: any) {
			setServerError(e.message || 'Ukjent feil')
		}
	}

	return (
		<FormProvider {...methods}>
			<Box
				as="form"
				onSubmit={methods.handleSubmit(onSubmit)}
				style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}
			>
				{serverError && <Alert variant="error">{serverError}</Alert>}

				<DollySelect
					isClearable={false}
					label="Type"
					size="medium"
					name="type"
					options={infostripeTypeOptions}
				/>

				<Textarea
					label="Melding"
					maxLength={500}
					{...methods.register('message', { required: 'Påkrevd' })}
					error={methods.formState.errors.message?.message}
					data-testid={TestComponentSelectors.INPUT_INFOSTRIPE_MESSAGE_EDIT}
				/>

				<div style={{ display: 'flex', gap: '1rem', marginTop: '4px' }}>
					<DollyDatepicker label="Start" name="start" />
					<DollyDatepicker label="Slutt" name="expires" />
				</div>

				<div style={{ display: 'flex', gap: '.5rem' }}>
					<Button
						type="submit"
						size="small"
						loading={methods.formState.isSubmitting}
						data-testid={TestComponentSelectors.BUTTON_SAVE_INFOSTRIPE}
					>
						Lagre
					</Button>
					<Button
						type="button"
						size="small"
						variant="secondary"
						disabled={methods.formState.isSubmitting}
						onClick={onDone}
						data-testid={TestComponentSelectors.BUTTON_CANCEL_EDIT_INFOSTRIPE}
					>
						Avbryt
					</Button>
				</div>
			</Box>
		</FormProvider>
	)
}
