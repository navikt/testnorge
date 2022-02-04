import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikProps } from 'formik'

interface AlderForm {
	formikBag: FormikProps<{}>
}
export const Alder = ({ formikBag }: AlderForm) => {
	const paths = {
		alder: 'pdldata.opprettNyPerson.alder',
		foedtEtter: 'pdldata.opprettNyPerson.foedtEtter',
		foedtFoer: 'pdldata.opprettNyPerson.foedtFoer',
	}

	const disableAlder =
		_get(formikBag.values, paths.foedtEtter) != null ||
		_get(formikBag.values, paths.foedtFoer) != null

	const disableFoedtDato = _get(formikBag.values, paths.alder) != ''

	return (
		<div className="flexbox--flex-wrap">
			{/*// @ts-ignore*/}
			<FormikTextInput name={paths.alder} type="number" label="Alder" disabled={disableAlder} />
			<FormikDatepicker
				name={paths.foedtEtter}
				label="Født etter"
				disabled={disableFoedtDato}
				fastfield={false}
			/>
			<FormikDatepicker
				name={paths.foedtFoer}
				label="Født før"
				disabled={disableFoedtDato}
				fastfield={false}
			/>
		</div>
	)
}
