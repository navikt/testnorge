import _get from 'lodash/get'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikProps } from 'formik'
import React from 'react'

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

	const disableFoedtDato =
		_get(formikBag.values, paths.alder) !== '' && _get(formikBag.values, paths.alder) !== null

	const onlyNumberKeyPressHandler = (event: React.KeyboardEvent<any>) =>
		!/\d/.test(event.key) && event.preventDefault()

	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name={paths.alder}
				type="number"
				onKeyPress={onlyNumberKeyPressHandler}
				label="Alder"
				isDisabled={disableAlder}
			/>
			<FormikDatepicker
				name={paths.foedtEtter}
				label="Født etter"
				disabled={disableFoedtDato}
				maxDate={new Date()}
				fastfield={false}
			/>
			<FormikDatepicker
				name={paths.foedtFoer}
				label="Født før"
				disabled={disableFoedtDato}
				maxDate={new Date()}
				fastfield={false}
			/>
		</div>
	)
}
