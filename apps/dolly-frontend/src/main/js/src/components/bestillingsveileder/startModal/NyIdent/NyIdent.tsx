import React from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

const validationSchema = Yup.object({
	antall: Yup.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette {min} person')
		.max(50, 'Kan kun bestille max 50 identer om gangen.')
		.required('Oppgi antall personer'),
	identtype: Yup.string().required('Velg en identtype'),
})

const initialValues = {
	antall: '1',
	identtype: Options('identtype')[0].value,
	mal: null,
}

export type NyBestillingProps = {
	brukernavn: string
	onSubmit: (values: any, formMethods: any) => void
	onAvbryt: () => void
}

export function NyIdent({ onAvbryt, onSubmit }: NyBestillingProps) {
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: initialValues,
		resolver: yupResolver(validationSchema),
	})

	return (
		<FormProvider {...formMethods}>
			<div className="ny-bestilling-form">
				<h3>Velg type og antall</h3>
				<div className="ny-bestilling-form_selects">
					<FormSelect
						name="identtype"
						label="Velg identtype"
						size="medium"
						options={Options('identtype')}
						isClearable={false}
					/>
					<FormTextInput
						name="antall"
						label="Antall"
						type="number"
						size="medium"
						onBlur={(event) => formMethods.setValue('antall', event?.target?.value || '1')}
					/>
				</div>
				<ModalActionKnapper
					data-testid={TestComponentSelectors.BUTTON_START_BESTILLING}
					submitknapp="Start bestilling"
					disabled={!formMethods.formState.isValid || formMethods.formState.isSubmitting}
					onSubmit={() => onSubmit(formMethods.getValues(), formMethods)}
					onAvbryt={onAvbryt}
					center
				/>
			</div>
		</FormProvider>
	)
}
