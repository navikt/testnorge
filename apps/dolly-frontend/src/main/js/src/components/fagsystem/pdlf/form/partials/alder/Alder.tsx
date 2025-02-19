import * as _ from 'lodash-es'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export const Alder = ({ formMethods }) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formValues = formMethods.getValues()

	const paths = {
		alder: 'pdldata.opprettNyPerson.alder',
		foedtEtter: 'pdldata.opprettNyPerson.foedtEtter',
		foedtFoer: 'pdldata.opprettNyPerson.foedtFoer',
	}

	const harFoedsel = () => {
		const foedselListe = _.get(formValues, 'pdldata.person.foedsel')
		return foedselListe?.some((foedsel) => foedsel?.foedselsaar || foedsel?.foedselsdato)
	}

	const disableAlder =
		_.get(formValues, paths.foedtEtter) != null ||
		_.get(formValues, paths.foedtFoer) != null ||
		harFoedsel()

	const disableFoedtDato =
		(_.get(formValues, paths.alder) !== '' && _.get(formValues, paths.alder) !== null) ||
		harFoedsel()

	const onlyNumberKeyPressHandler = (event: React.KeyboardEvent<any>) =>
		!/\d/.test(event.key) && event.preventDefault()

	const minDateAlder = opts?.identtype === 'NPID' ? new Date('01.01.1870') : new Date('01.01.1900')

	return (
		<div className="flexbox--flex-wrap">
			<FormTextInput
				name={paths.alder}
				type="number"
				onKeyPress={onlyNumberKeyPressHandler}
				onBlur={(val) => {
					const value = val.target.value
					formMethods.setValue(paths.alder, value === '' ? null : value)
				}}
				label="Alder"
				isDisabled={disableAlder}
			/>
			<FormDatepicker
				name={paths.foedtEtter}
				label="Født etter"
				disabled={disableFoedtDato}
				maxDate={new Date()}
				minDate={minDateAlder}
			/>
			<FormDatepicker
				name={paths.foedtFoer}
				label="Født før"
				disabled={disableFoedtDato}
				maxDate={new Date()}
				minDate={minDateAlder}
			/>
		</div>
	)
}
