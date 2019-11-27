import React, { useState } from 'react'
import { Formik } from 'formik'
import * as yup from 'yup'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { ModalActions } from '../ModalActions'

import './nyIdent.less'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: ''
}

const validationSchema = yup.object({
	antall: yup
		.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette 1 testperson')
		.required('Oppgi antall testbrukere'),
	identtype: yup.string().required('Velg en identtype')
})

export const NyIdent = ({ onAvbryt, onSubmit }) => (
	<Formik initialValues={initialValues} validationSchema={validationSchema} onSubmit={onSubmit}>
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
					<FormikSelect name="mal" label="Maler" options={[]} size="grow" />
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
