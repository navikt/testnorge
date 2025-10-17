import React, { useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { Button, Textarea } from '@navikt/ds-react'
import { ToastContainer } from 'react-toastify'
import { ErrorToast } from '@/components/ui/toast/ErrorToast'
import { SuccessToast } from '@/components/ui/toast/SuccessToast'
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

const createDefaultValues = (): InfostripeFormValues => ({
	type: 'INFO',
	message: '',
	start: new Date(),
	expires: addDays(new Date(), 7),
})

export const CreateInfostripeForm: React.FC = () => {
	const { createInfostripe } = useDollyInfostriper()
	const [serverError, setServerError] = useState<string | null>(null)
	const [success, setSuccess] = useState<boolean>(false)
	const [formVersion, setFormVersion] = useState(0)

	const formMethods = useForm<InfostripeFormValues>({ defaultValues: createDefaultValues() })
	const { handleSubmit, reset, control, formState } = formMethods

	const onSubmit = async (data: InfostripeFormValues) => {
		setServerError(null)
		setSuccess(false)
		try {
			await createInfostripe(data)
			setSuccess(true)
			reset(createDefaultValues())
			setFormVersion((v) => v + 1)
		} catch (e: any) {
			setServerError(e.message || 'Ukjent feil')
		}
	}

	return (
		<FormProvider {...formMethods}>
			<form onSubmit={handleSubmit(onSubmit)}>
				<h2>Opprett ny infostripe</h2>

				<Controller
					name="type"
					control={control}
					rules={{ required: 'Påkrevd' }}
					render={({ field }) => (
						<DollySelect
							label="Type"
							size="medium"
							isClearable={false}
							options={infostripeTypeOptions}
							value={infostripeTypeOptions.find((o) => o.value === field.value) || null}
							onChange={(option: any) => field.onChange(option ? option.value : null)}
							onBlur={field.onBlur}
							name={field.name}
						/>
					)}
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
					<DollyDatepicker key={`start-${formVersion}`} label="Start" name="start" />
					<DollyDatepicker key={`expires-${formVersion}`} label="Slutt" name="expires" />
				</div>

				<div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', marginTop: '1rem' }}>
					<Button type="submit" loading={formState.isSubmitting}>
						Opprett
					</Button>
					<Button
						type="button"
						variant="secondary"
						disabled={formState.isSubmitting}
						onClick={() => {
							reset(createDefaultValues())
							setFormVersion((v) => v + 1)
							setServerError(null)
							setSuccess(false)
						}}
					>
						Nullstill
					</Button>
				</div>
				<ErrorToast applicationError={serverError} />
				<SuccessToast message={success ? 'Infostripe opprettet' : null} />
				<ToastContainer containerId="global-toast" theme="light" />
			</form>
		</FormProvider>
	)
}
