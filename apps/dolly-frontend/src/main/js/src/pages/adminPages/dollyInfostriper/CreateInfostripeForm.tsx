import React, { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import {
	Button,
	HStack,
	InlineMessage,
	Select,
	Textarea,
	TextField,
	VStack,
} from '@navikt/ds-react'
import { useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { InfostripeDatepicker } from '@/pages/adminPages/dollyInfostriper/InfostripeDatepicker'
import { addDays, format } from 'date-fns'

export const infostripeTypeOptions = [
	{ label: 'Info', value: 'INFO' },
	{ label: 'Advarsel', value: 'WARNING' },
	{ label: 'Feil', value: 'ERROR' },
	{ label: 'Suksess', value: 'SUCCESS' },
]

export type InfostripeFormValues = {
	type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
	title: string
	message: string
	start?: Date
	expires?: Date
}

export const INFOSTRIPE_TITLE_MAX_LENGTH = 100

export const toLocalDateTime = (date?: Date) =>
	date ? format(date, "yyyy-MM-dd'T'HH:mm:ss") : null

const createDefaultValues = (): InfostripeFormValues => ({
	type: 'INFO',
	title: '',
	message: '',
	start: new Date(),
	expires: addDays(new Date(), 7),
})

export const CreateInfostripeForm: React.FC = () => {
	const { createInfostripe } = useDollyInfostriper()
	const [serverError, setServerError] = useState<string | null>(null)
	const [success, setSuccess] = useState<boolean>(false)
	const [formVersion, setFormVersion] = useState(0)

	const { handleSubmit, reset, control, formState } = useForm<InfostripeFormValues>({
		defaultValues: createDefaultValues(),
	})

	const onSubmit = async (data: InfostripeFormValues) => {
		setServerError(null)
		setSuccess(false)
		try {
			await createInfostripe({
				...data,
				start: toLocalDateTime(data.start),
				expires: toLocalDateTime(data.expires),
			})
			setSuccess(true)
			reset(createDefaultValues())
			setFormVersion((v) => v + 1)
		} catch (e: any) {
			setServerError(e.message || 'Ukjent feil')
		}
	}

	return (
		<form onSubmit={handleSubmit(onSubmit)}>
			<h2>Opprett ny infostripe</h2>
			<VStack gap="space-16">
				<Controller
					name="type"
					control={control}
					rules={{ required: 'Påkrevd' }}
					render={({ field, fieldState }) => (
						<Select
							{...field}
							label="Type"
							error={fieldState.error?.message}
							style={{ maxWidth: '200px' }}
						>
							{infostripeTypeOptions.map((option) => (
								<option key={option.value} value={option.value}>
									{option.label}
								</option>
							))}
						</Select>
					)}
				/>

				<Controller
					name="title"
					control={control}
					rules={{
						maxLength: {
							value: INFOSTRIPE_TITLE_MAX_LENGTH,
							message: `Maks ${INFOSTRIPE_TITLE_MAX_LENGTH} tegn`,
						},
					}}
					render={({ field, fieldState }) => (
						<TextField
							{...field}
							label="Tittel"
							maxLength={INFOSTRIPE_TITLE_MAX_LENGTH}
							error={fieldState.error?.message}
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

				<HStack gap="space-16">
					<InfostripeDatepicker
						key={`start-${formVersion}`}
						name="start"
						label="Start"
						control={control}
					/>
					<InfostripeDatepicker
						key={`expires-${formVersion}`}
						name="expires"
						label="Slutt"
						control={control}
					/>
				</HStack>

				<HStack gap="space-12" align="center" marginBlock="space-8 space-0">
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
					{serverError && (
						<InlineMessage status="error">{serverError.replace(/\?\S*/, '')}</InlineMessage>
					)}
					{success && <InlineMessage status="success">Infostripe opprettet</InlineMessage>}
				</HStack>
			</VStack>
		</form>
	)
}
