import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { AdresseKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Diverse } from './partials/Diverse'
import { Alder } from './partials/alder/Alder'
import { Vergemaal } from './partials/vergemaal/Vergemaal'
import { Fullmakt } from './partials/fullmakt/Fullmakt'

const alderPaths = ['tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer', 'tpsf.doedsdato']

const nasjonalitetPaths = [
	'tpsf.statsborgerskap',
	'tpsf.innvandretFraLand',
	'tpsf.utvandretTilLand'
]

const diversePaths = [
	'tpsf.kjonn',
	'tpsf.identtype',
	'tpsf.harMellomnavn',
	'tpsf.sivilstand',
	'tpsf.sprakKode',
	'tpsf.egenAnsattDatoFom',
	'tpsf.egenAnsattDatoTom',
	'tpsf.spesreg',
	'tpsf.utenFastBopel',
	'tpsf.erForsvunnet',
	'tpsf.harBankkontonr',
	'tpsf.telefonnummer_1'
]

const vergemaalPath = ['tpsf.vergemaal']
const fullmaktPath = ['tpsf.fullmakt']

const panelPaths = [alderPaths, nasjonalitetPaths, diversePaths, vergemaalPath, fullmaktPath].flat()

export const Personinformasjon = ({ formikBag }) => (
	<Vis attributt={panelPaths}>
		<Panel
			heading="Personinformasjon"
			hasErrors={panelError(formikBag, panelPaths)}
			iconType={'personinformasjon'}
			startOpen={() =>
				erForste(
					formikBag.values,
					alderPaths.concat(nasjonalitetPaths, diversePaths, vergemaalPath, fullmaktPath)
				)
			}
		>
			<Kategori title="Alder" vis={alderPaths}>
				<Alder basePath="tpsf" formikBag={formikBag} />
			</Kategori>

			<Kategori title="Nasjonalitet" vis={nasjonalitetPaths}>
				<Vis attributt="tpsf.statsborgerskap">
					<FormikSelect
						name="tpsf.statsborgerskap"
						label="Statsborgerskap"
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
						size="large"
						isClearable={false}
					/>
					<FormikDatepicker name="tpsf.statsborgerskapRegdato" label="Statsborgerskap fra" />
					<FormikDatepicker name="tpsf.statsborgerskapTildato" label="Statsborgerskap til" />
				</Vis>

				<Vis attributt="tpsf.innvandretFraLand">
					<FormikSelect
						name="tpsf.innvandretFraLand"
						label="Innvandret fra"
						kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
						size="large"
						isClearable={false}
					/>
					<FormikDatepicker name="tpsf.innvandretFraLandFlyttedato" label="Innvandret dato" />
				</Vis>

				<Vis attributt="tpsf.utvandretTilLand">
					<FormikSelect
						name="tpsf.utvandretTilLand"
						label="Utvandret til"
						kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
						size="large"
						isClearable={false}
					/>
					<FormikDatepicker name="tpsf.utvandretTilLandFlyttedato" label="Utvandret dato" />
				</Vis>
			</Kategori>

			<Kategori title="Diverse" vis={diversePaths}>
				<Diverse formikBag={formikBag} />
			</Kategori>
			<Kategori title="Vergemål" vis={vergemaalPath}>
				<Vergemaal />
			</Kategori>
			<Kategori title="Fullmakt" vis={fullmaktPath}>
				<Fullmakt />
			</Kategori>
		</Panel>
	</Vis>
)
