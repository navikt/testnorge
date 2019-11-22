import _mapValues from 'lodash/mapValues'
import { ArenaApi, InstApi } from '~/service/Api'

const addId = obj =>
	_mapValues(obj, (value, key) => ({
		...value,
		id: key
	}))

export const PANELER = addId({
	personinfo: {
		label: 'Personinformasjon',
		infotekst:
			'Data om institusjonsopphold blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må derfor velges i siste steg.',
		tilgjengeligeMiljoeEndepunkt: InstApi.getTilgjengeligeMiljoer
	},
	adresser: { label: 'Adresser' },
	relasjoner: { label: 'Familierelasjoner' },
	arbeidInntekt: {
		label: 'Arbeid og inntekt',
		infotekst:
			'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.'
	},
	krr: {
		label: 'Kontakt- og reservasjonsregisteret',
		infotekst:
			'KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke. I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret. Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost.'
	},
	krrdod: {
		label: 'Kontaktinformasjon for dødsbo',
		infotekst:
			'Kontaktinformasjon for dødsbo blir kun distribuert til Q2, og dette miljøet må derfor velges i siste steg.'
	},
	arena: {
		label: 'Arena',
		infotekst:
			'Arena-data blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må derfor velges i siste steg.',
		tilgjengeligeMiljoeEndepunkt: ArenaApi.getTilgjengeligeMiljoe
	},
	udi: { label: 'UDI' }
})

export const KATEGORIER = addId({
	alder: { label: 'Alder' },
	nasjonalitet: { label: 'Nasjonalitet' },
	instdata: { label: 'Instutisjonsopphold' },
	identifikasjon: { label: 'Identifikasjon' },
	diverse: { label: 'Diverse' },
	boadresse: { label: 'Boadresse' },
	postadresse: { label: 'Postadresse' },
	partner: { label: 'Partner' },
	barn: { label: 'Barn' },
	arbeidsforhold: { label: 'Arbeidsforhold' },
	inntekt: { label: 'Inntekt' },
	krr: { label: 'Kontakt- og reservasjonsregisteret' },
	krrdod: { label: 'Kontaktinformasjon for dødsbo' },
	arena: { label: 'Arena' },
	opphold: { label: 'Gjeldende oppholdsstatus' },
	arbeidsadgang: { label: 'Arbeidsadgang' },
	alias: { label: 'Alias' },
	annet: { label: 'Annet' }
})

export const ATTRIBUTTER = [
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.alder,
		path: 'tpsf.foedtFoer',
		label: 'Født før',
		name: 'foedtFoer'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.alder,
		path: 'tpsf.foedtEtter',
		label: 'Født etter',
		name: 'foedtEtter'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.alder,
		path: 'tpsf.doedsdato',
		label: 'Dødsdato',
		name: 'doedsdato'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.nasjonalitet,
		path: 'tpsf.statsborgerskap',
		label: 'Statsborgerskap',
		name: 'statsborgerskap'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.nasjonalitet,
		path: 'tpsf.innvandretFraLand',
		label: 'Innvandret fra',
		name: 'innvandretFraLand'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.nasjonalitet,
		path: 'tpsf.utvandretTilLand',
		label: 'Utvandret til',
		name: 'utvandretTilLand'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.instdata,
		path: 'instdata',
		label: 'Har instutisjonsopphold',
		name: 'instdata'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.identifikasjon,
		path: 'tpsf.identHistorikk',
		label: 'Har identhistorikk',
		name: 'identHistorikk'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.identifikasjon,
		path: 'tpsf.utenlandsid',
		label: 'Har utenlands-id',
		name: 'utenlandsid'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.identifikasjon,
		path: 'tpsf.falskidentitet',
		label: 'Har falsk identitet',
		name: 'falskidentitet'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.kjonn',
		label: 'Kjønn',
		name: 'kjonn'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.harMellomnavn',
		label: 'Mellomnavn',
		name: 'harMellomnavn'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.sivilstand',
		label: 'Sivilstand',
		name: 'sivilstand'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.sprakKode',
		label: 'Språk',
		name: 'sprakKode'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.egenAnsattDatoFom',
		label: 'Egenansatt',
		name: 'egenAnsattDatoFom'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.spesreg',
		label: 'Diskresjonskode',
		name: 'spesreg'
	},
	{
		panel: PANELER.personinfo,
		kategori: KATEGORIER.diverse,
		path: 'tpsf.erForsvunnet',
		label: 'Forsvunnet',
		name: 'erForsvunnet'
	},
	{
		panel: PANELER.adresser,
		kategori: KATEGORIER.boadresse,
		path: 'tpsf.boadresse',
		label: 'Har boadresse',
		name: 'boadresse'
	},
	{
		panel: PANELER.adresser,
		kategori: KATEGORIER.postadresse,
		path: 'tpsf.postadresse',
		label: 'Har postadresse',
		name: 'postadresse'
	},
	{
		panel: PANELER.relasjoner,
		kategori: KATEGORIER.partner,
		path: 'tpsf.partner',
		label: 'Har partner',
		name: 'partner'
	},
	{
		panel: PANELER.relasjoner,
		kategori: KATEGORIER.barn,
		path: 'tpsf.barn',
		label: 'Har barn',
		name: 'barn'
	},
	{
		panel: PANELER.arbeidInntekt,
		kategori: KATEGORIER.arbeidsforhold,
		path: 'tpsf.arbeidsforhold',
		label: 'Har arbeidsforhold',
		name: 'arbeidsforhold'
	},
	{
		panel: PANELER.arbeidInntekt,
		kategori: KATEGORIER.inntekt,
		path: 'sigrunstub',
		label: 'Har inntekt',
		name: 'inntekt'
	},
	{
		panel: PANELER.krr,
		kategori: KATEGORIER.krr,
		path: 'krrstub',
		label: 'Har kontaktinformasjon',
		name: 'kontaktinformasjon'
	},
	{
		panel: PANELER.krrdod,
		kategori: KATEGORIER.krrdod,
		path: 'pdlforvalter.kontaktinformasjonForDoedsbo',
		label: 'Har kontaktinformasjon for dødsbo',
		name: 'kontaktinformasjonForDoedsbo'
	},
	{
		panel: PANELER.arena,
		kategori: KATEGORIER.arena,
		path: 'arenaforvalter',
		label: 'Aktiver/inaktiver bruker',
		name: 'arenaforvalter'
	},
	{
		panel: PANELER.udi,
		kategori: KATEGORIER.opphold,
		path: 'udistub.oppholdStatus',
		label: 'Har oppholdsstatus',
		name: 'oppholdStatus'
	},
	{
		panel: PANELER.udi,
		kategori: KATEGORIER.alias,
		path: 'udistub.aliaser',
		label: 'Har aliaser',
		name: 'aliaser'
	},
	{
		panel: PANELER.udi,
		kategori: KATEGORIER.alias,
		path: 'udistub.annet',
		label: 'Annet',
		name: 'annet'
	}
]
