import React, { useContext } from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { isAfter } from 'date-fns'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

type NomFormProps = {
	formikBag: FormikProps<{}>
}

const navAnsattPaths = [
	'nomData.opprettNavIdent',
	'tpsMessaging.egenAnsattDatoFom',
	'tpsMessaging.egenAnsattDatoTom',
]

export const NomForm = ({ formikBag }: NomFormProps) => {
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

	const settFormikDate = (value: string, path: string) => {
		formikBag.setFieldValue(`tpsMessaging.${path}`, value)
		formikBag.setFieldValue(`skjerming.${path}`, value)
	}

	const harSkjerming = HarAktivSkjerming()

	return (
		<Vis attributt={navAnsattPaths}>
			<Panel
				heading="NAV-ansatt"
				iconType="logo"
				//@ts-ignore
				startOpen={() => erForste(formikBag.values, navAnsattPaths)}
			>
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name="nomData.opprettNavIdent"
						label="Har NAV-ident"
						options={Options('boolean')}
						visHvisAvhuket
					/>

					<FormikDatepicker
						name="tpsMessaging.egenAnsattDatoFom"
						label="Skjerming fra"
						disabled={harSkjerming}
						onChange={(date: string) => {
							settFormikDate(date, 'egenAnsattDatoFom')
						}}
						visHvisAvhuket
					/>
					{harSkjerming && (
						<FormikDatepicker
							name="tpsMessaging.egenAnsattDatoTom"
							label="Skjerming til"
							onChange={(date: string) => {
								settFormikDate(date, 'egenAnsattDatoTom')
							}}
							visHvisAvhuket
						/>
					)}
				</div>
			</Panel>
		</Vis>
	)
}
