import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const TpsMessagingDiverse = ({ formikBag }) => {
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
		formikBag.setFieldValue(`tpsMessaging.${path}`, value)
		formikBag.setFieldValue(`skjerming.${path}`, value)
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect
				name="tpsMessaging.spraakKode"
				label="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				size="large"
				visHvisAvhuket
			/>

			<FormikDatepicker
				name="tpsMessaging.egenAnsattDatoFom"
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
					name="tpsMessaging.egenAnsattDatoTom"
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
