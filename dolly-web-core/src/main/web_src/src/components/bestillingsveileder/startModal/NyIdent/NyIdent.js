import React from 'react'
import { Formik } from 'formik'
import * as yup from 'yup'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { ModalActions } from '../ModalActions'

import './nyIdent.less'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: null
}

const validationSchema = yup.object({
	antall: yup
		.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette 1 person')
		.required('Oppgi antall personer'),
	identtype: yup.string().required('Velg en identtype')
})

export const NyIdent = ({ onAvbryt, onSubmit }) => {
	const state = useAsync(async () => {
		const response = await DollyApi.getBestillingMaler()
		return response.data
	}, [])

	const malOptions = state.value
		? state.value.map(mal => ({
				value: mal.malNavn,
				label: mal.malNavn,
				data: mal
		  }))
		: []

	const preSubmit = (values, formikBag) => {
		if (values.mal) values.mal = malOptions.find(m => m.value === values.mal).data
		return onSubmit(values, formikBag)
	}

	return (
		<Formik initialValues={initialValues} validationSchema={validationSchema} onSubmit={preSubmit}>
			{formikBag => (
				<div className="ny-ident-form">
					<div className="ny-ident-form_selects">
						<FormikSelect
							name="identtype"
							label="Velg identtype"
							size="grow"
							options={Options('identtype')}
							isClearable={false}
						/>
						<FormikSelect
							name="mal"
							label="Maler"
							isLoading={state.loading}
							options={malOptions}
							size="grow"
						/>
					</div>
					<div className="ny-ident-form_antall">
						<FormikTextInput name="antall" label="Antall" type="number" />
					</div>
					<ModalActions
						disabled={!formikBag.isValid || formikBag.isSubmitting}
						onSubmit={formikBag.handleSubmit}
						onAvbryt={onAvbryt}
					/>
				</div>
			)}
		</Formik>
	)
}
