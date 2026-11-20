import React, { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Button, HStack, InfoCard, Select, Textarea, TextField, VStack } from '@navikt/ds-react'
import { XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import { InfoStripeType, useDollyInfostriper } from '@/utils/hooks/useDollyInfostriper'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	INFOSTRIPE_TITLE_MAX_LENGTH,
	InfostripeFormValues,
	infostripeTypeOptions,
	toLocalDateTime,
} from '@/pages/adminPages/dollyInfostriper/CreateInfostripeForm'
import { InfostripeDatepicker } from '@/pages/adminPages/dollyInfostriper/InfostripeDatepicker'

interface Props {
	stripe: InfoStripeType
	onDone(): void
}

const toDate = (value?: Date | string | null) => (value ? new Date(value) : undefined)

export const EditInfostripeForm: React.FC<Props> = ({ stripe, onDone }) => {
	const { updateInfostripe } = useDollyInfostriper()
	const [serverError, setServerError] = useState<string | null>(null)

	const { handleSubmit, control, formState } = useForm<InfostripeFormValues>({
		defaultValues: {
			type: stripe.type,
			title: stripe.title ?? '',
			message: stripe.message,
			start: toDate(stripe.start),
			expires: toDate(stripe.expires),
		},
	})

	const onSubmit = async (data: InfostripeFormValues) => {
		setServerError(null)
		try {
			await updateInfostripe({
				id: stripe.id,
				type: data.type,
				title: data.title,
				message: data.message.trim(),
				start: toLocalDateTime(data.start),
				expires: toLocalDateTime(data.expires),
			})
			onDone()
		} catch (e: any) {
			setServerError(e.message || 'Ukjent feil')
		}
	}

	return (
		<form onSubmit={handleSubmit(onSubmit)}>
			<VStack gap="space-16">
				{serverError && (
					<InfoCard data-color="danger">
						<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
							{serverError}
						</InfoCard.Message>
					</InfoCard>
				)}

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
							data-testid={TestComponentSelectors.INPUT_INFOSTRIPE_MESSAGE_EDIT}
						/>
					)}
				/>

				<HStack gap="space-16">
					<InfostripeDatepicker name="start" label="Start" control={control} required={false} />
					<InfostripeDatepicker name="expires" label="Slutt" control={control} required={false} />
				</HStack>

				<HStack gap="space-12" align="center" marginBlock="space-8 space-0">
					<Button
						type="submit"
						loading={formState.isSubmitting}
						data-testid={TestComponentSelectors.BUTTON_SAVE_INFOSTRIPE}
					>
						Lagre
					</Button>
					<Button
						type="button"
						variant="secondary"
						disabled={formState.isSubmitting}
						onClick={onDone}
						data-testid={TestComponentSelectors.BUTTON_CANCEL_EDIT_INFOSTRIPE}
					>
						Avbryt
					</Button>
				</HStack>
			</VStack>
		</form>
	)
}
