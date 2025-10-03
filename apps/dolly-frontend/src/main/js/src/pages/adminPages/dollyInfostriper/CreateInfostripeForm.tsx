import React, { useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { Alert, Button, Textarea } from '@navikt/ds-react'
import { useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { addDays } from 'date-fns'

const infostripeTypeOptions = [
	{ label: 'Info', value: 'INFO' },
	{ label: 'Advarsel', value: 'WARNING' },
	{ label: 'Feil', value: 'ERROR' },
	{ label: 'Suksess', value: 'SUCCESS' },
]

export type InfostripeFormValues = {
	type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
	message: string
	start: Date
	expires: Date
}

const defaultValues: InfostripeFormValues = {
	type: 'INFO',
	message: '',
	start: new Date(),
	expires: addDays(new Date(), 7),
}

export const CreateInfostripeForm: React.FC = () => {
	const { createInfostripe } = useDollyInfostriper()
	const [serverError, setServerError] = useState<string | null>(null)
	const [success, setSuccess] = useState<boolean>(false)

	const formMethods = useForm<InfostripeFormValues>({ defaultValues })
	const { handleSubmit, reset, control, formState } = formMethods

	const onSubmit = async (data: InfostripeFormValues) => {
		setServerError(null)
		setSuccess(false)
		try {
			await createInfostripe(data)
			setSuccess(true)
			reset(defaultValues)
		} catch (e: any) {
			setServerError(e.message || 'Ukjent feil')
		}
	}

	return (
		<FormProvider {...formMethods}>
			<form onSubmit={handleSubmit(onSubmit)}>
				<h2>Opprett ny infostripe</h2>
				{success && <Alert variant="success">Infostripe opprettet</Alert>}
				{serverError && <Alert variant="error">{serverError}</Alert>}

				<DollySelect
					isClearable={false}
					label="Type"
					size="medium"
					name="type"
					options={infostripeTypeOptions}
				/>

				<Controller
					name="message"
					control={control}
					rules={{
						required: 'Påkrevd',
						maxLength: { value: 500, message: 'Maks 500 tegn' },
					}}
					render={({ field, fieldState }) => (
						<Textarea
							{...field}
							label="Melding"
							maxLength={500}
							error={fieldState.error?.message}
						/>
					)}
				/>

				<div style={{ display: 'flex', marginTop: '10px' }}>
					<DollyDatepicker label="Start" name="start" />
					<DollyDatepicker label="Slutt" name="expires" />
				</div>

				<div style={{ display: 'flex', gap: '0.5rem' }}>
					<Button type="submit" loading={formState.isSubmitting}>
						Opprett
					</Button>
					<Button
						type="button"
						variant="secondary"
						disabled={formState.isSubmitting}
						onClick={() => reset(defaultValues)}
					>
						Nullstill
					</Button>
				</div>
			</form>
		</FormProvider>
	)
}
