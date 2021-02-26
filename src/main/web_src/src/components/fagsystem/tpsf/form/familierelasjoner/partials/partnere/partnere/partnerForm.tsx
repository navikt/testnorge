import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import { Sivilstand } from '../sivilstand/Sivilstand'
import { Partner } from './partnerTypes'

interface PartnerForm {
	path: string
	formikBag: FormikProps<{}>
	partner: Partner
	locked: boolean
	minDatoSivilstand: string
}

export default ({ path, formikBag, partner, ...rest }: PartnerForm) => {
	const handleDoedsdatoChange = (dato: Date) => {
		const sivilstander = _get(formikBag.values, `${path}.sivilstander`)
		const sisteSivilstand =
			(sivilstander.length > 0 && sivilstander[sivilstander.length - 1].sivilstand) ||
			partner.sivilstand ||
			null

		if (dato) {
			formikBag.setFieldValue(`${path}.doedsdato`, dato)

			if (sisteSivilstand === 'GIFT') {
				_get(formikBag.values, `${path}.sivilstander`).push({
					sivilstand: 'ENKE',
					sivilstandRegdato: dato
				})
			} else if (sisteSivilstand === 'ENKE') {
				sivilstander.length > 0 &&
					formikBag.setFieldValue(`${path}.sivilstander[${sivilstander.length - 1}]`, {
						sivilstand: 'ENKE',
						sivilstandRegdato: dato
					})
			} else if (sisteSivilstand === 'REPA') {
				_get(formikBag.values, `${path}.sivilstander`).push({
					sivilstand: 'GJPA',
					sivilstandRegdato: dato
				})
			} else if (sisteSivilstand === 'GJPA') {
				sivilstander.length > 0 &&
					formikBag.setFieldValue(`${path}.sivilstander[${sivilstander.length - 1}]`, {
						sivilstand: 'GJPA',
						sivilstandRegdato: dato
					})
			}
		} else {
			formikBag.setFieldValue(`${path}.doedsdato`, null)
		}
	}

	return (
		<>
			{partner.ny ? (
				<>
					<FormikSelect
						name={`${path}.identtype`}
						label="Identtype"
						options={Options('identtype')}
						isClearable={false}
					/>
					<FormikSelect
						name={`${path}.kjonn`}
						label="Kjønn"
						kodeverk={PersoninformasjonKodeverk.Kjoennstyper}
					/>
					<FormikCheckbox
						name={`${path}.harFellesAdresse`}
						label="Har felles adresse"
						checkboxMargin
					/>
					<FormikSelect
						name={`${path}.statsborgerskap`}
						label="Statsborgerskap"
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
					/>
					<FormikDatepicker name={`${path}.statsborgerskapRegdato`} label="Statsborgerskap fra" />
					<FormikDatepicker name={`${path}.statsborgerskapTildato`} label="Statsborgerskap til" />
					<Diskresjonskoder basePath={path} formikBag={formikBag} />
					<Alder
						basePath={path}
						formikBag={formikBag}
						title="Alder"
						handleDoed={handleDoedsdatoChange}
					/>
				</>
			) : (
				<>
					<h4>
						{partner.fornavn} {partner.etternavn} ({partner.ident})
					</h4>
					<div className="alder-component">
						<FormikDatepicker
							name={`${path}.doedsdato`}
							label="Dødsdato"
							onChange={(dato: Date) => handleDoedsdatoChange(dato)}
						/>
					</div>
				</>
			)}
			<Sivilstand
				sivilstander={partner.sivilstander}
				formikBag={formikBag}
				basePath={`${path}.sivilstander`}
				{...rest}
			/>
		</>
	)
}
