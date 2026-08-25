export const folkeregisteretPaths = {
	identifikasjonOgStatus: [
		'identifikator',
		'identifikatorType',
		'foedselsdato.fraOgMed',
		'foedselsdato.tilOgMed',
		'doedsdato.fraOgMed',
		'doedsdato.tilOgMed',
		'kjoenn',
		'personstatus',
		'sivilstand',
		'identitetsgrunnlagStatus',
		'adressebeskyttelse',
		'harFalskIdentitet',
		'utenlandskPersonIdentifikasjon',
		'harLegitimasjonsdokument',
	],
	statsborgerskap: [
		'harNorskStatsborgerskap',
		'harFlereStatsborgerskap',
		'harNordenStatsborgerskap',
		'harEuEoesStatsborgerskap',
		'harTredjelandStatsborgerskap',
		'harUtgaattStatsborgerskap',
		'harStatsborgerskapHistorikk',
	],
	navn: [
		'navn.navnLengde.fraOgMed',
		'navn.navnLengde.tilOgMed',
		'navn.harFlereFornavn',
		'navn.harNavnSpesialtegn',
		'navn.harMellomnavn',
	],
	adresser: [
		'adresser.adresseGradering',
		'adresser.kommunenummer',
		'adresser.harAdresseSpesialtegn',
		'adresser.harBostedsadresse',
		'avansert.harBostedsadresseHistorikk',
		'adresser.harOppholdAnnetSted',
		'adresser.harPostadresseNorge',
		'adresser.harPostadresseUtland',
		'adresser.harKontaktadresseDoedsbo',
	],
	relasjoner: [
		'relasjoner.relasjon',
		'relasjoner.antallBarn.fraOgMed',
		'relasjoner.antallBarn.tilOgMed',
		'relasjoner.relasjonMedFoedselsaar.fraOgMed',
		'relasjoner.relasjonMedFoedselsaar.tilOgMed',
		'relasjoner.harForeldreAnsvar',
		'relasjoner.harDeltBosted',
		'relasjoner.harVergemaalEllerFremtidsfullmakt',
		'relasjoner.borMedMor',
		'relasjoner.borMedFar',
		'relasjoner.borMedMedmor',
		'relasjoner.foreldreHarSammeAdresse',
	],
	hendelser: ['hendelser.hendelse', 'hendelser.sisteHendelse'],
}

export const skattPaths = {
	beregnetSkatt: [
		'beregnetSkatt.inntektsaar',
		'beregnetSkatt.oppgjoerstype',
		'beregnetSkatt.pensjonsgivendeInntekt',
	],
	summertSkattegrunnlag: [
		'summertSkattegrunnlag.inntektsaar',
		'summertSkattegrunnlag.stadietype',
		'summertSkattegrunnlag.oppgjoerstype',
		'summertSkattegrunnlag.tekniskNavn',
		'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.fraOgMed',
		'summertSkattegrunnlag.alminneligInntektFoerSaerfradragBeloep.tilOgMed',
	],
	skattemelding: ['skattemelding.inntektsaar', 'skattemelding.skattemeldingstype'],
}

export const arbeidInntektPaths = {
	arbeidsforhold: [
		'arbeidsforhold.startDatoPeriode.fraOgMed',
		'arbeidsforhold.startDatoPeriode.tilOgMed',
		'arbeidsforhold.sluttDatoPeriode.fraOgMed',
		'arbeidsforhold.sluttDatoPeriode.tilOgMed',
		'arbeidsforhold.harPermisjoner',
		'arbeidsforhold.harPermitteringer',
		'arbeidsforhold.harArbeidsgiver',
		'arbeidsforhold.harTimerMedTimeloenn',
		'arbeidsforhold.harUtenlandsopphold',
		'arbeidsforhold.harHistorikk',
		'arbeidsforhold.arbeidsforholdstype',
	],
	inntektAordningen: [
		'inntekt.periode.fraOgMed',
		'inntekt.periode.tilOgMed',
		'inntekt.opplysningspliktig',
		'inntekt.inntektstyper',
		'inntekt.forskuddstrekk',
		'inntekt.beskrivelse',
		'inntekt.harHistorikk',
	],
}

export const pensjonPaths = {
	tjenestepensjonsavtale: [
		'tjenestepensjonsavtale.pensjonsinnretningOrgnr',
		'tjenestepensjonsavtale.periode',
	],
}

export const virksomhetPaths = {
	enhetsregisteretForetaksregisteret: ['roller'],
}

export const tabPaths = {
	folkeregisteret: Object.values(folkeregisteretPaths).flat(),
	skatt: Object.values(skattPaths).flat(),
	arbeidinntekt: Object.values(arbeidInntektPaths).flat(),
	pensjon: Object.values(pensjonPaths).flat(),
	virksomhet: Object.values(virksomhetPaths).flat(),
}
