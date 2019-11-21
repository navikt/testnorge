import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Arbeidsadgang = ({ formikBag }) => {
	const arbeidsadgangObj = formikBag.values.udistub.arbeidsadgang
	const arbeidsadgangInitialValues = formikBag.initialValues.udistub.arbeidsadgang

	const endreArbeidsadgang = v => {
		formikBag.setFieldValue('udistub.arbeidsadgang.harArbeidsAdgang', v.value)
		if (v.value !== 'JA') {
			formikBag.setFieldValue('udistub.arbeidsadgang.arbeidsOmfang', '')
			formikBag.setFieldValue('udistub.arbeidsadgang.periode', arbeidsadgangInitialValues.periode)
			formikBag.setFieldValue('udistub.arbeidsadgang.typeArbeidsadgang', '')
		}
	}

	return (
		<Kategori title="Arbeidsadgang">
			<FormikSelect
				name="udistub.arbeidsadgang.harArbeidsAdgang"
				label="Har arbeidsadgang"
				options={Options('jaNeiUavklart')}
				value={arbeidsadgangObj.harArbeidsAdgang}
				onChange={endreArbeidsadgang}
				isClearable={false}
			/>
			{formikBag.values.udistub.arbeidsadgang.harArbeidsAdgang === 'JA' && (
				<React.Fragment>
					<FormikSelect
						name="udistub.arbeidsadgang.typeArbeidsadgang"
						label="Type arbeidsadgang"
						options={Options('typeArbeidsadgang')}
					/>
					<FormikSelect
						name="udistub.arbeidsadgang.arbeidsOmfang"
						label="Arbeidsomfang"
						options={Options('arbeidsOmfang')}
					/>
					<FormikDatepicker
						name="udistub.arbeidsadgang.periode.fra"
						label="Arbeidsadgang fra dato"
					/>
					<FormikDatepicker
						name="udistub.arbeidsadgang.periode.til"
						label="Arbeidsadgang til dato"
					/>
				</React.Fragment>
			)}
		</Kategori>
	)
}
