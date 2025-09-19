import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { Sikkerhetstiltak } from '@/components/fagsystem/pdlf/form/partials/sikkerhetstiltak/Sikkerhetstiltak'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Telefonnummer } from '@/components/fagsystem/pdlf/form/partials/telefonnummer/Telefonnummer'
import { Doedsfall } from '@/components/fagsystem/pdlf/form/partials/doedsfall/Doedsfall'
import { Statsborgerskap } from '@/components/fagsystem/pdlf/form/partials/statsborgerskap/Statsborgerskap'
import { Innvandring } from '@/components/fagsystem/pdlf/form/partials/innvandring/Innvandring'
import { Utvandring } from '@/components/fagsystem/pdlf/form/partials/utvandring/Utvandring'
import { TilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/form/partials/tilrettelagtkommunikasjon/TilrettelagtKommunikasjon'
import { Alder } from '@/components/fagsystem/pdlf/form/partials/alder/Alder'
import { Kjoenn } from '@/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { Navn } from '@/components/fagsystem/pdlf/form/partials/navn/Navn'
import { Vergemaal } from '@/components/fagsystem/pdlf/form/partials/vergemaal/Vergemaal'
import { NorskBankkonto, UtenlandskBankkonto } from '@/components/fagsystem/bankkonto/form'
import { Foedested } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedested'
import { Foedselsdato } from '@/components/fagsystem/pdlf/form/partials/foedsel/Foedselsdato'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'
import { NomForm } from '@/components/fagsystem/nom/form/NomForm'

const foedselPaths = ['pdldata.person.foedested', 'pdldata.person.foedselsdato']

const nasjonalitetPaths = [
	'pdldata.person.statsborgerskap',
	'pdldata.person.innflytting',
	'pdldata.person.utflytting',
]

const diversePaths = ['skjerming.egenAnsattDatoFom', 'skjerming.egenAnsattDatoTom']

const alderPaths = [
	'pdldata.opprettNyPerson.alder',
	'pdldata.opprettNyPerson.foedtEtter',
	'pdldata.opprettNyPerson.foedtFoer',
]

const utenlandskBankkontoPath = ['bankkonto.utenlandskBankkonto']
const norskBankkontoPath = ['bankkonto.norskBankkonto']
const kjoennPath = ['pdldata.person.kjoenn']
const navnPath = ['pdldata.person.navn']
const telefonnummerPath = ['pdldata.person.telefonnummer']
const tilrettelagtKommunikasjonPath = ['pdldata.person.tilrettelagtKommunikasjon']
const innvandringPath = ['pdldata.person.innflytting']
const utvandringPath = ['pdldata.person.utflytting']
const statsborgerskapPath = ['pdldata.person.statsborgerskap']
const foedestedPath = ['pdldata.person.foedested']
const foedselsdatoPath = ['pdldata.person.foedselsdato']
const doedsfallPath = ['pdldata.person.doedsfall']
const vergemaalPath = ['pdldata.person.vergemaal']
const sikkerhetstiltakPath = ['pdldata.person.sikkerhetstiltak']
const fullmaktPath = ['pdldata.person.fullmakt', 'fullmakt']
const nomPath = ['nomdata']

const panelPaths = [
	alderPaths,
	nasjonalitetPaths,
	diversePaths,
	innvandringPath,
	utvandringPath,
	kjoennPath,
	navnPath,
	telefonnummerPath,
	tilrettelagtKommunikasjonPath,
	foedselPaths,
	doedsfallPath,
	vergemaalPath,
	sikkerhetstiltakPath,
	statsborgerskapPath,
	utenlandskBankkontoPath,
	norskBankkontoPath,
	nomPath,
].flat()

export const Personinformasjon = ({ formMethods }) => {
	const { personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType

	return (
		<Vis attributt={panelPaths}>
			<Panel
				heading="Personinformasjon"
				hasErrors={panelError(panelPaths)}
				iconType={'personinformasjon'}
				startOpen={erForsteEllerTest(formMethods.getValues(), panelPaths)}
			>
				{!personFoerLeggTil && (
					<Kategori title="Alder (grunnlag for fødselsnummer)" vis={alderPaths}>
						<Alder formMethods={formMethods} />
					</Kategori>
				)}

				<Kategori title="Fødsel" vis={foedselPaths}>
					<Vis attributt={foedestedPath}>
						<Foedested formMethods={formMethods} />
					</Vis>
					<Vis attributt={foedselsdatoPath}>
						<Foedselsdato formMethods={formMethods} />
					</Vis>
				</Kategori>

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

				<Kategori title="Kjønn" vis={kjoennPath}>
					<Kjoenn />
				</Kategori>

				<Kategori title="Navn" vis={navnPath}>
					<Navn formMethods={formMethods} />
				</Kategori>

				<Kategori title="Diverse" vis={diversePaths}>
					<SkjermingForm formMethods={formMethods} />
				</Kategori>

				<Kategori title="Norsk bankkonto" vis={norskBankkontoPath}>
					<NorskBankkonto formMethods={formMethods} />
				</Kategori>

				<Kategori title="Utenlandsk bankkonto" vis={utenlandskBankkontoPath}>
					<UtenlandskBankkonto formMethods={formMethods} />
				</Kategori>

				<Kategori title="Telefonnummer" vis={telefonnummerPath}>
					<Telefonnummer formMethods={formMethods} />
				</Kategori>

				<Kategori title="Vergemål" vis={vergemaalPath}>
					<Vergemaal formMethods={formMethods} />
				</Kategori>

				<Kategori title="Sikkerhetstiltak" vis={sikkerhetstiltakPath}>
					<Sikkerhetstiltak formMethods={formMethods} />
				</Kategori>

				<Kategori title="Tilrettelagt kommunikasjon" vis={tilrettelagtKommunikasjonPath}>
					<TilrettelagtKommunikasjon />
				</Kategori>

				<Kategori title="NAV-ansatt (NOM)" vis={nomPath}>
					<NomForm />
				</Kategori>
			</Panel>
		</Vis>
	)
}
