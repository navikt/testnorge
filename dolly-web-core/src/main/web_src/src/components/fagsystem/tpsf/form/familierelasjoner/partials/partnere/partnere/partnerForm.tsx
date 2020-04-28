import React from 'react'
import _isEmpty from 'lodash/isEmpty'
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
	vurderFjernePartner: () => void
}

export default ({ path, formikBag, partner, ...rest }: PartnerForm) => (
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
				<Diskresjonskoder basePath={path} formikBag={formikBag} />
				<Alder basePath={path} formikBag={formikBag} title="Alder" />
			</>
		) : (
			<h4>
				{partner.fornavn} {partner.etternavn} ({partner.ident})
			</h4>
		)}
		<Sivilstand
			sivilstander={partner.sivilstander}
			formikBag={formikBag}
			basePath={`${path}.sivilstander`}
			{...rest}
		/>
	</>
)
