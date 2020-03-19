import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import {
	DollyFieldArrayWrapper,
	DollyFaBlokk,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import Formatters from '~/utils/DataFormatter'
import { Sivilstand } from './Sivilstand'
import { erOpprettNyPartnerGyldig } from './SivilstandOptions'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: null }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(30, 60),
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: ''
}

const ugyldigSivilstandState = errors =>
	_get(errors, 'tpsf.relasjoner.partnere', []).some(partner => _has(partner, 'sivilstander'))

const sistePartner = (partnere = []) => partnere[partnere.length - 1]

const sisteSivilstand = (partner = {}) => {
	const sivilstander = partner.sivilstander || []
	return sivilstander[sivilstander.length - 1]
}

// Det er 3 kriterier for å opprette ny partner
// - Må ha regDato for forholder (error validering sjekker om dato er gyldig)
// - Må ha sivilstandKode som er gyldig som "siste forholdstype"
// - Må ikke finnes errors i sivilstandform
const sjekkKanOppretteNyPartner = (partnere, formikBag) => {
	const { sivilstand, sivilstandRegdato } = sisteSivilstand(sistePartner(partnere))
	const gyldigKode = erOpprettNyPartnerGyldig(sivilstand)
	const harRegdato = sivilstandRegdato !== null
	const harGyldigSivilstander = !ugyldigSivilstandState(formikBag.errors)
	return harGyldigSivilstander && gyldigKode && harRegdato
}

const path = 'tpsf.relasjoner.partnere'
export const Partnere = ({ formikBag }) => (
	<FieldArray name={path}>
		{arrayHelpers => {
			const partnere = _get(arrayHelpers.form.values, path, [])
			const kanOppretteNyPartner = sjekkKanOppretteNyPartner(partnere, formikBag)
			const addNewEntry = () => arrayHelpers.push(initialValues)

			return (
				<DollyFieldArrayWrapper header="Partner">
					{partnere.map((c, idx) => {
						const isLast = idx === partnere.length - 1

						// Det er kun mulig å slette siste forhold
						const showRemove = isLast && idx > 0
						const clickRemove = () => arrayHelpers.remove(idx)
						return (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								header="Partner"
								handleRemove={showRemove && clickRemove}
							>
								<PartnerForm
									path={path}
									idx={idx}
									formikBag={formikBag}
									locked={idx !== partnere.length - 1}
								/>
							</DollyFaBlokk>
						)
					})}
					<FieldArrayAddButton
						hoverText={
							!kanOppretteNyPartner
								? 'Forhold med tidligere partner må avsluttes (skilt eller enke/-mann)'
								: false
						}
						addEntryButtonText="Legg til ny partner"
						onClick={addNewEntry}
						disabled={!kanOppretteNyPartner}
					/>
				</DollyFieldArrayWrapper>
			)
		}}
	</FieldArray>
)

const PartnerForm = ({ path, idx, formikBag, locked }) => {
	const basePath = `${path}[${idx}]`
	const erSistePartner = _get(formikBag.values, path).length === idx + 1
	return (
		<>
			<FormikSelect
				name={`${basePath}.identtype`}
				label="Identtype"
				options={Options('identtype')}
				isClearable={false}
			/>
			<FormikSelect name={`${basePath}.kjonn`} label="Kjønn" kodeverk="Kjønnstyper" />
			<FormikCheckbox
				name={`${basePath}.harFellesAdresse`}
				label="Har felles adresse"
				checkboxMargin
			/>
			<FormikSelect
				name={`${basePath}.statsborgerskap`}
				label="Statsborgerskap"
				kodeverk="Landkoder"
			/>
			<FormikDatepicker name={`${basePath}.statsborgerskapRegdato`} label="Statsborgerskap fra" />
			<Diskresjonskoder basePath={basePath} formikBag={formikBag} />
			<Alder basePath={basePath} formikBag={formikBag} title="Alder" />
			<Sivilstand
				formikBag={formikBag}
				basePath={`${basePath}.sivilstander`}
				locked={locked}
				erSistePartner={erSistePartner}
			/>
		</>
	)
}
