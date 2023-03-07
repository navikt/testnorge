import * as _ from 'lodash-es'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikProps } from 'formik'
import React from 'react'
import _get from 'lodash/get'

interface AlderForm {
	formikBag: FormikProps<{}>
}

export const Alder = ({ formikBag }: AlderForm) => {
	const paths = {
		alder: 'pdldata.opprettNyPerson.alder',
		foedtEtter: 'pdldata.opprettNyPerson.foedtEtter',
		foedtFoer: 'pdldata.opprettNyPerson.foedtFoer',
	}

	const harFoedsel = () => {
		const foedselListe = _get(formikBag.values, 'pdldata.person.foedsel')
		return foedselListe?.some((foedsel) => foedsel?.foedselsaar || foedsel?.foedselsdato)
	}

	const disableAlder =
		_.get(formikBag.values, paths.foedtEtter) != null ||
		_.get(formikBag.values, paths.foedtFoer) != null ||
		harFoedsel()

	const disableFoedtDato =
		(_.get(formikBag.values, paths.alder) !== '' &&
			_.get(formikBag.values, paths.alder) !== null) ||
		harFoedsel()

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
