import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Diverse } from './partials/Diverse'

const attrPaths = [
	pathAttrs.kategori.alder,
	pathAttrs.kategori.nasjonalitet,
	pathAttrs.kategori.diverse,
	['tpsf.identHistorikk']
].flat()

export const Personinformasjon = ({ formikBag }) => (
	<Vis attributt={attrPaths}>
		<Panel heading="Personinformasjon" hasErrors={panelError(formikBag)} startOpen>
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

			<Diverse formikBag={formikBag} />
		</Panel>
	</Vis>
)
