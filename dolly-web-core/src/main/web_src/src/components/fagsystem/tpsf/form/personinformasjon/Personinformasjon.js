import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Diverse } from './partials/Diverse'

const alderPaths = ['tpsf.foedtEtter', 'tpsf.foedtFoer', 'tpsf.doedsdato']

const nasjonalitetPaths = [
	'tpsf.statsborgerskap',
	'tpsf.innvandretFraLand',
	'tpsf.utvandretTilLand'
]
const diversePaths = [
	'tpsf.kjonn',
	'tpsf.harMellomnavn',
	'tpsf.sivilstand',
	'tpsf.sprakKode',
	'tpsf.egenAnsattDatoFom',
	'tpsf.spesreg',
	'tpsf.erForsvunnet'
]

const panelPaths = [alderPaths, nasjonalitetPaths, diversePaths].flat()

export const Personinformasjon = ({ formikBag }) => (
	<Vis attributt={panelPaths}>
		<Panel
			heading="Personinformasjon"
			hasErrors={panelError(formikBag)}
			iconType={'information-circle'}
			startOpen
		>
			<Kategori title="Alder" vis={alderPaths}>
				<FormikDatepicker name="tpsf.foedtEtter" label="Født etter" />
				<FormikDatepicker name="tpsf.foedtFoer" label="Født før" />
				<FormikDatepicker name="tpsf.doedsdato" label="Dødsdato" />
			</Kategori>

			<Kategori title="Nasjonalitet" vis={nasjonalitetPaths}>
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
