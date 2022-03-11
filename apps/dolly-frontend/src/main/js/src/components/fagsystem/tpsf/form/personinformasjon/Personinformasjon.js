import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { Diverse } from './partials/Diverse'
import { Vergemaal } from './partials/vergemaal/Vergemaal'
import { Fullmakt } from './partials/fullmakt/Fullmakt'
import { Sikkerhetstiltak } from '~/components/fagsystem/pdlf/form/partials/sikkerhetstiltak/Sikkerhetstiltak'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import _get from 'lodash/get'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { TpsMessagingDiverse } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/tpsmessaging/TpsMessagingDiverse'
import { Telefonnummer } from '~/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { Doedsfall } from '~/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import { Statsborgerskap } from '~/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { Innvandring } from '~/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { Utvandring } from '~/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { TilrettelagtKommunikasjon } from '~/components/fagsystem/pdlf/form/partials/tilrettelagtkommunikasjon/TilrettelagtKommunikasjon'

const alderPaths = ['tpsf.alder', 'tpsf.foedtEtter', 'tpsf.foedtFoer', 'tpsf.doedsdato']

const nasjonalitetPaths = [
	'pdldata.person.statsborgerskap',
	'pdldata.person.innflytting',
	'pdldata.person.utflytting',
]

const diversePaths = [
	'tpsf.kjonn',
	'tpsf.identtype',
	'tpsf.harMellomnavn',
	'tpsf.harNyttNavn',
	'tpsf.sivilstand',
	'tpsf.sprakKode',
	'tpsf.spesreg',
	'tpsf.utenFastBopel',
	'tpsf.erForsvunnet',
	'tpsMessaging.utenlandskBankkonto',
	'tpsMessaging.norskBankkonto',
	'tpsMessaging.sikkerhetstiltak',
	'tpsMessaging.spraakKode',
]

const telefonnummerPath = ['pdldata.person.telefonnummer']
const tilrettelagtKommunikasjonPath = ['pdldata.person.tilrettelagtKommunikasjon']
const innvandringPath = ['pdldata.person.innflytting']
const utvandringPath = ['pdldata.person.utflytting']
const statsborgerskapPath = ['pdldata.person.statsborgerskap']
const doedsfallPath = ['pdldata.person.doedsfall']
const vergemaalPath = ['tpsf.vergemaal']
const fullmaktPath = ['pdldata.person.fullmakt']

const sikkerhetstiltakPaths = [
	'pdldata.person.sikkerhetstiltak',
	'tpsf.typeSikkerhetTiltak',
	'tpsf.beskrSikkerhetTiltak',
	'tpsf.sikkerhetTiltakDatoFom',
	'tpsf.sikkerhetTiltakDatoTom',
]

const panelPaths = [
	alderPaths,
	nasjonalitetPaths,
	diversePaths,
	innvandringPath,
	utvandringPath,
	telefonnummerPath,
	tilrettelagtKommunikasjonPath,
	doedsfallPath,
	vergemaalPath,
	fullmaktPath,
	sikkerhetstiltakPaths,
	statsborgerskapPath,
].flat()

export const Personinformasjon = ({ formikBag }) => {
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)
	return (
		<Vis attributt={panelPaths}>
			<Panel
				heading="Personinformasjon"
				hasErrors={panelError(formikBag, panelPaths)}
				iconType={'personinformasjon'}
				startOpen={() =>
					erForste(
						formikBag.values,
						alderPaths.concat(
							nasjonalitetPaths,
							diversePaths,
							vergemaalPath,
							fullmaktPath,
							doedsfallPath,
							statsborgerskapPath
						)
					)
				}
			>
				{(!personFoerLeggTil || _get(formikBag.touched, 'pdldata.person.doedsfall.doedsdato')) && (
					<Kategori title="Alder" vis={alderPaths}>
						<Alder basePath="tpsf" formikBag={formikBag} />
					</Kategori>
				)}

				<Kategori title="Dødsfall" vis={doedsfallPath}>
					<Doedsfall />
				</Kategori>

				<Kategori title="Nasjonalitet" vis={nasjonalitetPaths}>
					<Vis attributt={statsborgerskapPath}>
						<Statsborgerskap />
					</Vis>

					<Kategori title="Innvandring" vis={innvandringPath}>
						<Innvandring />
					</Kategori>

					<Kategori title="Utvandring" vis={utvandringPath}>
						<Utvandring />
					</Kategori>
				</Kategori>
				<Kategori title="Diverse" vis={diversePaths}>
					<Diverse formikBag={formikBag} />
					<TpsMessagingDiverse formikBag={formikBag} />
				</Kategori>
				<Kategori title="Telefonnummer" vis={telefonnummerPath}>
					<Telefonnummer formikBag={formikBag} />
				</Kategori>
				<Kategori title="Vergemål" vis={vergemaalPath}>
					<Vergemaal />
				</Kategori>
				<Kategori title="Fullmakt" vis={fullmaktPath}>
					<Fullmakt formikBag={formikBag} />
				</Kategori>
				<Kategori title="Sikkerhetstiltak" vis={sikkerhetstiltakPaths}>
					<Sikkerhetstiltak formikBag={formikBag} />
				</Kategori>
				<Kategori title="Tilrettelagt kommunikasjon" vis={tilrettelagtKommunikasjonPath}>
					<TilrettelagtKommunikasjon />
				</Kategori>
			</Panel>
		</Vis>
	)
}
