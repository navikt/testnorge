import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { validation } from '@/components/fagsystem/skjermingsregister/form/validation'

export const SkjermingForm = () => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)

	const harAktivSkjerming = () => {
		if (personFoerLeggTil?.skjermingsregister?.skjermetTil) {
			return personFoerLeggTil?.skjermingsregister?.skjermetFra
				? isAfter(
						new Date(personFoerLeggTil?.skjermingsregister?.skjermetTil?.substring(0, 10)),
						new Date(),
					)
				: false
		} else {
			return personFoerLeggTil?.skjermingsregister?.hasOwnProperty('skjermetFra')
		}
	}

	const harSkjerming = harAktivSkjerming()

	return (
		<div className="flexbox--flex-wrap">
			<FormDatepicker
				name="skjerming.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				maxDate={new Date()}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormDatepicker name="skjerming.egenAnsattDatoTom" label="Skjerming til" visHvisAvhuket />
			)}
		</div>
	)
}

SkjermingForm.validation = validation
