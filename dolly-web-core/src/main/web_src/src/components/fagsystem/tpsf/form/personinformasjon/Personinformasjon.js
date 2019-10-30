import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { IdentHistorikk } from './partials/IdentHistorikk'
import { Diverse } from './partials/Diverse'

export const Personinformasjon = ({ formikProps }) => {
	return (
		<Panel heading="Personinformasjon" hasErrors={panelError(formikProps)}>
			<Kategori title="Alder">
				<FormikDatepicker name="tpsf.foedtEtter" label="Født etter" />
				<FormikDatepicker name="tpsf.foedtFoer" label="Født før" />
				<FormikDatepicker name="tpsf.doedsdato" label="Dødsdato" />
			</Kategori>

			<Kategori title="Nasjonalitet">
				<FormikSelect name="tpsf.statsborgerskap" label="Statsborgerskap" kodeverk="Landkoder" />
				<FormikDatepicker name="tpsf.statsborgerskapRegdato" label="Statsborgerskap fra" />

				<FormikSelect name="tpsf.innvandretFraLand" label="Innvandret" kodeverk="Landkoder" />
				<FormikDatepicker name="tpsf.innvandretFraLandFlyttedato" label="Innvandret dato" />

				<FormikSelect name="tpsf.utvandretTilLand" label="Utvandret" kodeverk="Landkoder" />
				<FormikDatepicker name="tpsf.utvandretTilLandFlyttedato" label="Utvandret dato" />
			</Kategori>

			<IdentHistorikk formikProps={formikProps} />

			<Diverse formikProps={formikProps} />
		</Panel>
	)
}
