import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { validation } from '@/components/fagsystem/skjermingsregister/form/validation'

export const SkjermingForm = ({ formikBag }) => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)

	const HarAktivSkjerming = () => {
		if (personFoerLeggTil?.skjermingsregister?.skjermetTil) {
			return personFoerLeggTil?.skjermingsregister?.skjermetFra
				? isAfter(
						new Date(personFoerLeggTil?.skjermingsregister?.skjermetTil?.substring(0, 10)),
						new Date()
				  )
				: false
		} else {
			return personFoerLeggTil?.skjermingsregister?.hasOwnProperty('skjermetFra')
		}
	}

	const settFormikDate = (value, path) => {
		formikBag.setFieldValue(`skjerming.${path}`, value)
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<div className="flexbox--flex-wrap">
			<FormikDatepicker
				name="skjerming.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				onChange={(date) => {
					settFormikDate(date, 'egenAnsattDatoFom')
				}}
				maxDate={new Date()}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormikDatepicker
					name="skjerming.egenAnsattDatoTom"
					label="Skjerming til"
					onChange={(date) => {
						settFormikDate(date, 'egenAnsattDatoTom')
					}}
					visHvisAvhuket
				/>
			)}
		</div>
	)
}

SkjermingForm.validation = validation
