import React from 'react'
import * as Yup from 'yup'
import Panel from '~/components/ui/panel/Panel'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const initialValues = {
	arenaforvalter: {
		// aap: [
		// 	{
		// 		fraDato: '',
		// 		tilDato: ''
		// 	}
		// ],
		// aap115: [
		// 	{
		// 		fraDato: ''
		// 	}
		// ],
		arenaBrukertype: '',
		inaktiveringDato: '',
		kvalifiseringsgruppe: ''
	},
	temp: {
		har115vedtak: '',
		harAAPvedtak: ''
	}
}

export const validation = {
	arenaforvalter: {
		aap: [
			{
				fraDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
				tilDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
			}
		],
		aap115: [
			{
				fraDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
			}
		],
		arenaBrukertype: Yup.string().required('Vennligst velg'),
		inaktiveringDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		kvalifiseringsgruppe: ''
	}
}

export const ArenaForm = ({ formikProps }) => {
	console.log('formikProps :', formikProps)
	return (
		<React.Fragment>
			<Panel heading="Arena" hasErrors={panelError(formikProps)}>
				<FormikSelect
					name="arenaforvalter.arenaBrukertype"
					label="Brukertype"
					options={Options('arenaBrukertype')}
				/>
				<FormikSelect
					name="arenaforvalter.kvalifiseringsgruppe"
					label="Servicebehov"
					options={Options('kvalifiseringsgruppe')}
					isDisabled={formikProps.values.arenaforvalter.arenaBrukertype !== 'MED_SERVICEBEHOV'}
				/>
				<FormikDatepicker
					name="arenaforvalter.inaktiveringDato"
					label="Inaktiv fra dato"
					disabled={formikProps.values.arenaforvalter.arenaBrukertype !== 'UTEN_SERVICEBEHOV'}
				/>
				{formikProps.values.arenaforvalter.arenaBrukertype === 'MED_SERVICEBEHOV' && (
					<Kategori title="11-5-vedtak">
						<FormikSelect
							name="temp.har115vedtak"
							label="Har 11-5-vedtak"
							options={Options('boolean')}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap115[0].fraDato"
							label="Fra dato"
							disabled={!formikProps.values.temp.har115vedtak}
						/>
					</Kategori>
				)}
				{formikProps.values.arenaforvalter.arenaBrukertype === 'MED_SERVICEBEHOV' && (
					<Kategori title="AAP-vedtak UA - positivt utfall">
						<FormikSelect
							name="temp.harAAPvedtak"
							label="Har AAP-vedtak"
							options={Options('boolean')}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap[0].fraDato"
							label="Fra dato"
							disabled={!formikProps.values.temp.harAAPvedtak}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap[0].tilDato"
							label="Til dato"
							disabled={!formikProps.values.temp.harAAPvedtak}
						/>
					</Kategori>
				)}
			</Panel>
		</React.Fragment>
	)
}
