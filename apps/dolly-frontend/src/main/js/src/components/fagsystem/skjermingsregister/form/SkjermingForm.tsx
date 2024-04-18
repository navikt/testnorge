import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { validation } from '@/components/fagsystem/skjermingsregister/form/validation'

export const SkjermingForm = ({ formMethods }) => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)

	const HarAktivSkjerming = () => {
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

	const settFormDate = (value, path) => {
		formMethods.setValue(`skjerming.${path}`, value)
		formMethods.trigger(`skjerming.${path}`)
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<div className="flexbox--flex-wrap">
			<FormDatepicker
				name="skjerming.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				onChange={(date) => {
					settFormDate(date, 'egenAnsattDatoFom')
				}}
				maxDate={new Date()}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormDatepicker
					name="skjerming.egenAnsattDatoTom"
					label="Skjerming til"
					onChange={(date) => {
						settFormDate(date, 'egenAnsattDatoTom')
					}}
					visHvisAvhuket
				/>
			)}
		</div>
	)
}

SkjermingForm.validation = validation
