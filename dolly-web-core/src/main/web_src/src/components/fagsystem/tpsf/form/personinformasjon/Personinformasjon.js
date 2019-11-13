import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { IdentHistorikk } from './partials/IdentHistorikk'
import { Diverse } from './partials/Diverse'

export const Personinformasjon = ({ formikBag }) => (
	<Vis attributt={pathAttrs.panel.personinfo}>
		<Panel heading="Personinformasjon" hasErrors={panelError(formikBag)}>
			<Kategori title="Alder" vis={pathAttrs.kategori.alder}>
				<FormikDatepicker name="tpsf.foedtEtter" label="Født etter" />
				<FormikDatepicker name="tpsf.foedtFoer" label="Født før" />
				<FormikDatepicker name="tpsf.doedsdato" label="Dødsdato" />
			</Kategori>

			<Kategori title="Nasjonalitet" vis={pathAttrs.kategori.nasjonalitet}>
				<Vis attributt="tpsf.statsborgerskap">
					<FormikSelect name="tpsf.statsborgerskap" label="Statsborgerskap" kodeverk="Landkoder" />
					<FormikDatepicker name="tpsf.statsborgerskapRegdato" label="Statsborgerskap fra" />
				</Vis>

				<Vis attributt="tpsf.innvandretFraLand">
					<FormikSelect name="tpsf.innvandretFraLand" label="Innvandret" kodeverk="Landkoder" />
					<FormikDatepicker name="tpsf.innvandretFraLandFlyttedato" label="Innvandret dato" />
				</Vis>

				<Vis attributt="tpsf.utvandretTilLand">
					<FormikSelect name="tpsf.utvandretTilLand" label="Utvandret" kodeverk="Landkoder" />
					<FormikDatepicker name="tpsf.utvandretTilLandFlyttedato" label="Utvandret dato" />
				</Vis>
			</Kategori>

			<Vis attributt="tpsf.identHistorikk">
				<IdentHistorikk formikBag={formikBag} />
			</Vis>

			<Diverse formikBag={formikBag} />
		</Panel>
	</Vis>
)
