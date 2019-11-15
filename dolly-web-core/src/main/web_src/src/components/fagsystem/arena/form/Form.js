import React, { useState } from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const ArenaForm = ({ formikBag }) => {
	const [har115vedtak, setHar115vedtak] = useState(Boolean(formikBag.values.arenaforvalter.aap115))
	const [harAAPvedtak, setHarAAPvedtak] = useState(Boolean(formikBag.values.arenaforvalter.aap))

	const fjernAttributt = attributt => {
		const nyFormikBag = formikBag.values.arenaforvalter
		delete nyFormikBag[attributt]
		formikBag.setFieldValue('arenaforvalter', nyFormikBag)
	}

	const check115vedtak = () => {
		if (har115vedtak) {
			setHar115vedtak(false)
			fjernAttributt('aap115')
		} else {
			setHar115vedtak(true)
			formikBag.setFieldValue('arenaforvalter.aap115', [{ fraDato: '' }])
		}
	}

	const checkAAPvedtak = () => {
		if (harAAPvedtak) {
			setHarAAPvedtak(false)
			fjernAttributt('aap')
		} else {
			setHarAAPvedtak(true)
			formikBag.setFieldValue('arenaforvalter.aap', [{ fraDato: '', tilDato: '' }])
		}
	}

	if (formikBag.values.arenaforvalter.arenaBrukertype !== 'MED_SERVICEBEHOV') {
		formikBag.values.arenaforvalter.kvalifiseringsgruppe = ''
		har115vedtak && check115vedtak()
		harAAPvedtak && checkAAPvedtak()
	} else if (formikBag.values.arenaforvalter.arenaBrukertype !== 'UTEN_SERVICEBEHOV') {
		formikBag.values.arenaforvalter.inaktiveringDato = ''
	}

	return (
		<React.Fragment>
			<Panel heading="Arena" hasErrors={panelError(formikBag)}>
				<Kategori title="">
					<FormikSelect
						name="arenaforvalter.arenaBrukertype"
						label="Brukertype"
						options={Options('arenaBrukertype')}
					/>
					<FormikSelect
						name="arenaforvalter.kvalifiseringsgruppe"
						label="Servicebehov"
						options={Options('kvalifiseringsgruppe')}
						isDisabled={formikBag.values.arenaforvalter.arenaBrukertype !== 'MED_SERVICEBEHOV'}
					/>
					<FormikDatepicker
						name="arenaforvalter.inaktiveringDato"
						label="Inaktiv fra dato"
						disabled={formikBag.values.arenaforvalter.arenaBrukertype !== 'UTEN_SERVICEBEHOV'}
					/>
				</Kategori>
				{formikBag.values.arenaforvalter.arenaBrukertype === 'MED_SERVICEBEHOV' && (
					<Kategori title="11-5-vedtak">
						<Checkbox
							id="har115vedtak"
							label="Har 11-5-vedtak"
							checked={har115vedtak}
							onChange={check115vedtak}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap115[0].fraDato"
							label="Fra dato"
							disabled={!har115vedtak}
						/>
					</Kategori>
				)}
				{formikBag.values.arenaforvalter.arenaBrukertype === 'MED_SERVICEBEHOV' && (
					<Kategori title="AAP-vedtak UA - positivt utfall">
						<Checkbox
							id="harAAPvedtak"
							label="Har AAP-vedtak"
							checked={harAAPvedtak}
							onChange={checkAAPvedtak}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap[0].fraDato"
							label="Fra dato"
							disabled={!harAAPvedtak}
						/>
						<FormikDatepicker
							name="arenaforvalter.aap[0].tilDato"
							label="Til dato"
							disabled={!harAAPvedtak}
						/>
					</Kategori>
				)}
			</Panel>
		</React.Fragment>
	)
}

ArenaForm.initialValues = {
	arenaforvalter: {
		arenaBrukertype: '',
		inaktiveringDato: '',
		kvalifiseringsgruppe: ''
	}
}

ArenaForm.validation = {
	arenaforvalter: Yup.object({
		aap: Yup.array().of(
			Yup.object({
				fraDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
				tilDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
			})
		),
		aap115: Yup.array().of(
			Yup.object({
				fraDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
			})
		),
		arenaBrukertype: Yup.string().required('Vennligst velg'),
		inaktiveringDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		kvalifiseringsgruppe: ''
	})
}
