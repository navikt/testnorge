import useBoolean from '@/utils/hooks/useBoolean'
import Loading from '@/components/ui/loading/Loading'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import PersonListeConnector from './PersonListe/PersonListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import { BestillingsveilederModal } from '@/components/bestillingsveileder/startModal/StartModal'
import Icon from '@/components/ui/icon/Icon'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import FinnPersonBestillingConnector from '@/pages/gruppeOversikt/FinnPersonBestillingConnector'
import { resetNavigering, resetPaginering } from '@/ducks/finnPerson'
import GruppeHeaderConnector from '@/pages/gruppe/GruppeHeader/GruppeHeaderConnector'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { useIkkeFerdigBestillingerGruppe } from '@/utils/hooks/useBestilling'
import StatusListeConnector from '@/components/bestilling/statusListe/StatusListeConnector'
import './Gruppe.less'
import { GruppeFeil, GruppeFeilmelding } from '@/pages/gruppe/GruppeFeil/GruppeFeilmelding'
import { ToggleGroup } from '@navikt/ds-react'

export const bestilling8098 = [
	{
		id: 8098,
		antallIdenter: 0,
		antallLevert: 0,
		ferdig: false,
		sistOppdatert: '2023-02-09T09:49:19.235405',
		bruker: {
			brukerId: '952ab92e-926f-4ac4-93d7-f2d552025caf',
			brukernavn: 'Traran, Betsy Carina',
			brukertype: 'AZURE',
			epost: 'Betsy.Carina.Traran@nav.no',
		},
		gruppeId: 307,
		stoppet: false,
		environments: ['q1'],
		status: [
			{
				id: 'PDL_FORVALTER',
				navn: 'Persondataløsningen (PDL)',
				statuser: [
					{
						melding: 'OK',
						identer: ['02488508388'],
					},
				],
			},
			{
				id: 'PDL_PERSONSTATUS',
				navn: 'PDL-Synkronisering',
				statuser: [
					{
						melding: 'OK',
						identer: ['02488508388'],
					},
				],
			},
			{
				id: 'AAREG',
				navn: 'Arbeidsregister (AAREG)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'ARENA',
				navn: 'Arena fagsystem',
				statuser: [
					{
						melding: 'Info: Oppretting startet mot Arena ...',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'UDISTUB',
				navn: 'Utlendingsdirektoratet (UDI)',
				statuser: [
					{
						melding: 'Info: Oppretting startet mot UdiStub ...',
						identer: ['02488508388'],
					},
				],
			},
			{
				id: 'INNTK',
				navn: 'Inntektskomponenten (INNTK)',
				statuser: [
					{
						melding: 'OK',
						identer: ['02488508388'],
					},
				],
			},
			{
				id: 'PEN_INNTEKT',
				navn: 'Pensjonsopptjening (POPP)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'TP_FORVALTER',
				navn: 'Tjenestepensjon (TP)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'PEN_FORVALTER',
				navn: 'Pensjon (PEN)',
				statuser: [
					{
						melding: 'OK',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
							{
								miljo: 'q2',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'PEN_AP',
				navn: 'Alderspensjon (AP)',
				statuser: [
					{
						melding: 'Feil:',
						detaljert: [
							{
								miljo: 'q1',
								identer: ['02488508388'],
							},
						],
					},
				],
			},
			{
				id: 'SYKEMELDING',
				navn: 'NAV Sykemelding',
				statuser: [
					{
						melding: 'Info: Venter på generering av sykemelding ...',
						identer: ['02488508388'],
					},
				],
			},
			{
				id: 'ANNEN_FEIL',
				navn: 'Annen Feil',
				statuser: [
					{
						melding: 'Feil: error:invalid_grant,',
						identer: ['02488508388'],
					},
				],
			},
		],
		bestilling: {
			krrstub: {
				gyldigFra: null,
				reservert: false,
				mobil: '34443357',
				epost: '',
				registrert: true,
				sdpAdresse: '',
				sdpLeverandoer: 1,
				spraak: 'nb',
			},
			instdata: [
				{
					institusjonstype: 'FO',
					startdato: '2023-01-31T00:00:00',
				},
			],
			aareg: [
				{
					genererPeriode: null,
					arbeidsforholdstype: 'ordinaertArbeidsforhold',
					arbeidsforholdId: null,
					ansettelsesPeriode: {
						fom: '2003-02-08T00:00:00',
						tom: null,
						sluttaarsak: null,
					},
					antallTimerForTimeloennet: [],
					arbeidsavtale: {
						antallKonverterteTimer: null,
						arbeidstidsordning: 'ikkeSkift',
						avtaltArbeidstimerPerUke: 37.5,
						avloenningstype: null,
						endringsdatoStillingsprosent: null,
						sisteLoennsendringsdato: null,
						stillingsprosent: 100,
						yrke: '2521106',
						ansettelsesform: 'fast',
						endringsdatoLoenn: null,
					},
					permittering: [],
					permisjon: [],
					fartoy: [],
					utenlandsopphold: [],
					arbeidsgiver: {
						aktoertype: 'ORG',
						orgnummer: '896929119',
					},
					navArbeidsforholdPeriode: null,
					isOppdatering: null,
					amelding: [],
				},
			],
			sigrunstub: [
				{
					grunnlag: [
						{
							tekniskNavn: 'formuePrimaerbolig',
							verdi: '45555',
						},
						{
							tekniskNavn: 'skatteoppgjoersdato',
							verdi: '2023-05-01',
						},
					],
					inntektsaar: '2023',
					svalbardGrunnlag: [],
					tjeneste: 'BEREGNET_SKATT',
				},
			],
			inntektstub: {
				inntektsinformasjon: [
					{
						antallMaaneder: 3,
						sisteAarMaaned: '2023-02',
						opplysningspliktig: '963743254',
						virksomhet: '896929119',
						inntektsliste: [
							{
								inntektstype: 'LOENNSINNTEKT',
								beloep: 54443,
								inngaarIGrunnlagForTrekk: true,
								utloeserArbeidsgiveravgift: true,
								fordel: 'naturalytelse',
								beskrivelse: 'kostDoegn',
								antall: 55,
							},
						],
					},
				],
			},
			arenaforvalter: {
				arenaBrukertype: 'MED_SERVICEBEHOV',
				kvalifiseringsgruppe: 'IKVAL',
				aap115: [
					{
						fraDato: '2023-02-03T00:00:00',
					},
				],
			},
			udistub: {
				oppholdStatus: {
					eosEllerEFTABeslutningOmOppholdsrett: 'FAMILIE',
					eosEllerEFTABeslutningOmOppholdsrettPeriode: {
						fra: '2023-02-02T00:00:00',
					},
				},
			},
			pensjonforvalter: {
				inntekt: {
					fomAar: 2013,
					tomAar: 2020,
					belop: 556566,
					redusertMedGrunnbelop: true,
				},
				tp: [
					{
						ordning: '3010',
						ytelser: [
							{
								type: 'ALDER',
								datoInnmeldtYtelseFom: '2023-01-09',
								datoYtelseIverksattFom: '2023-01-09',
								datoYtelseIverksattTom: null,
							},
						],
					},
				],
				alderspensjon: {
					iverksettelsesdato: '2023-03-01',
					uttaksgrad: 100,
					relasjoner: [
						{
							sumAvForvArbKapPenInntekt: null,
						},
					],
				},
			},
			inntektsmelding: {
				inntekter: [
					{
						aarsakTilInnsending: 'NY',
						arbeidsforhold: {
							arbeidsforholdId: '',
							beregnetInntekt: {
								beloep: 688888,
							},
						},
						arbeidsgiver: {
							virksomhetsnummer: '896929119',
						},
						avsendersystem: {
							innsendingstidspunkt: '2023-02-08T14:48:23',
						},
						naerRelasjon: false,
						refusjon: {},
						startdatoForeldrepengeperiode: '2023-02-01',
						ytelse: 'FORELDREPENGER',
					},
				],
				joarkMetadata: {
					tema: 'FOR',
				},
			},
			brregstub: {
				enheter: [
					{
						foretaksNavn: {
							navn1: 'Sauefabrikk',
						},
						forretningsAdresse: {
							adresse1: 'OPALV 75',
							kommunenr: '4601',
							landKode: 'NO',
							postnr: '5252',
							poststed: 'SØREIDGREND',
						},
						orgNr: 896929119,
						postAdresse: {
							adresse1: 'OPALV 75',
							kommunenr: '4601',
							landKode: 'NO',
							postnr: '5252',
							poststed: 'SØREIDGREND',
						},
						registreringsdato: '2023-02-08T14:49:04',
						rolle: 'BOBE',
						personroller: [],
					},
				],
				understatuser: [0],
			},
			dokarkiv: {
				tittel: 'Adresseendring for bruker bosatt i utlandet',
				tema: 'BID',
				kanal: 'SKAN_IM',
				dokumenter: [
					{
						tittel: 'Adresseendring for bruker bosatt i utlandet',
						brevkode: 'NAV 95-20.01',
					},
				],
			},
			sykemelding: {
				syntSykemelding: {
					orgnummer: '896929119',
					startDato: '2023-02-08T14:49:00',
				},
			},
			pdldata: {
				opprettNyPerson: {
					identtype: 'FNR',
					foedtEtter: null,
					foedtFoer: null,
					alder: null,
					syntetisk: true,
				},
				person: {
					kontaktinformasjonForDoedsbo: [
						{
							id: null,
							kilde: 'Dolly',
							master: 'FREG',
							folkeregistermetadata: null,
							skifteform: 'OFFENTLIG',
							attestutstedelsesdato: '2023-02-02T00:00:00',
							adresse: {
								adresselinje1: '',
								adresselinje2: '',
								postnummer: '',
								poststedsnavn: '',
								landkode: '',
							},
							advokatSomKontakt: {
								kontaktperson: {
									etternavn: 'ELG',
									fornavn: 'INTERNASJONAL',
									mellomnavn: 'MEMORERENDE',
									hasMellomnavn: null,
								},
								organisasjonsnavn: 'Delt ansvarlig',
								organisasjonsnummer: '996532912',
							},
							personSomKontakt: null,
							organisasjonSomKontakt: null,
						},
					],
					opphold: [
						{
							id: null,
							kilde: null,
							master: null,
							folkeregistermetadata: null,
							oppholdFra: '2023-02-02T00:00:00',
							oppholdTil: null,
							type: 'OPPLYSNING_MANGLER',
						},
					],
				},
			},
		},
	},
]

export type GruppeProps = {
	visning: string
	setVisning: Function
	sidetall: number
	sideStoerrelse: number
	sorting: object
	update: string
}

export enum VisningType {
	VISNING_PERSONER = 'personer',
	VISNING_BESTILLING = 'bestilling',
}

export default ({
	visning,
	setVisning,
	sidetall,
	sideStoerrelse,
	sorting,
	update,
}: GruppeProps) => {
	const { gruppeId } = useParams()
	const {
		currentBruker: { brukernavn, brukertype },
		loading: loadingBruker,
	} = useCurrentBruker()

	const { bestillingerById: ikkeFerdigBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		'personer',
		sidetall,
		sideStoerrelse,
		update
	)

	const { bestillingerById, loading: loadingBestillinger } = useIkkeFerdigBestillingerGruppe(
		gruppeId,
		visning,
		sidetall,
		sideStoerrelse,
		update
	)

	const {
		gruppe,
		identer,
		loading: loadingGruppe,
		// @ts-ignore
	} = useGruppeById(gruppeId, sidetall, sideStoerrelse, false, sorting?.kolonne, sorting?.retning)
	const [startBestillingAktiv, visStartBestilling, skjulStartBestilling] = useBoolean(false)

	const dispatch = useDispatch()
	const navigate = useNavigate()

	const bankIdBruker = brukertype === 'BANKID'

	if (loadingBruker || loadingGruppe || loadingBestillinger) {
		return <Loading label="Laster personer" panel />
	}

	if (!gruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.FETCH_FAILED} />
	}

	if (bankIdBruker && !gruppe?.erEierAvGruppe) {
		return <GruppeFeilmelding feil={GruppeFeil.ACCESS_DENIED} />
	}

	const byttVisning = (value: VisningType) => {
		dispatch(resetNavigering())
		dispatch(resetPaginering())
		setVisning(value)
	}
	// console.log('ikkeFerdigBestillinger: ', ikkeFerdigBestillinger) //TODO - SLETT MEG
	const startBestilling = (values: Record<string, unknown>) =>
		navigate(`/gruppe/${gruppeId}/bestilling`, { state: values })

	const erLaast = gruppe.erLaast
	return (
		<div className="gruppe-container">
			<GruppeHeaderConnector gruppe={gruppe} />
			{/*{ikkeFerdigBestillinger && (*/}
			{/*// @ts-ignore*/}
			{/*<StatusListeConnector gruppeId={gruppe.id} bestillingListe={ikkeFerdigBestillinger} />*/}
			<StatusListeConnector gruppeId={gruppe.id} bestillingListe={bestilling8098} />
			{/*)}*/}
			<div className="gruppe-toolbar">
				<div className="gruppe--full gruppe--flex-row-center">
					{!bankIdBruker && (
						<NavButton
							variant={'primary'}
							onClick={visStartBestilling}
							disabled={erLaast}
							title={
								erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''
							}
							className="margin-top-5 margin-bottom-5 margin-right-10"
						>
							Opprett personer
						</NavButton>
					)}

					<NavButton
						variant={bankIdBruker ? 'primary' : 'secondary'}
						onClick={() =>
							navigate(`/testnorge`, {
								state: {
									gruppe: gruppe,
								},
							})
						}
						disabled={erLaast}
						title={erLaast ? 'Denne gruppen er låst, og du kan ikke legge til flere personer.' : ''}
						className="margin-top-5 margin-bottom-5"
					>
						Importer personer
					</NavButton>

					<div style={{ flexGrow: '2' }}></div>

					{!bankIdBruker && <FinnPersonBestillingConnector />}
				</div>
				<div className="gruppe--flex-column-center margin-top-20 margin-bottom-10">
					<ToggleGroup size={'small'} value={visning} onChange={byttVisning}>
						<ToggleGroup.Item
							key={VisningType.VISNING_PERSONER}
							value={VisningType.VISNING_PERSONER}
						>
							<Icon
								key={VisningType.VISNING_PERSONER}
								size={13}
								kind={visning === VisningType.VISNING_PERSONER ? 'manLight' : 'man'}
							/>
							{`Personer (${gruppe.antallIdenter})`}
						</ToggleGroup.Item>
						<ToggleGroup.Item
							key={VisningType.VISNING_BESTILLING}
							value={VisningType.VISNING_BESTILLING}
						>
							<Icon
								key={VisningType.VISNING_BESTILLING}
								size={13}
								kind={visning === VisningType.VISNING_BESTILLING ? 'bestillingLight' : 'bestilling'}
							/>
							{`Bestillinger (${gruppe.antallBestillinger})`}
						</ToggleGroup.Item>
					</ToggleGroup>
				</div>
			</div>
			{startBestillingAktiv && (
				<BestillingsveilederModal
					onSubmit={startBestilling}
					onAvbryt={skjulStartBestilling}
					brukernavn={brukernavn}
				/>
			)}
			{visning === VisningType.VISNING_PERSONER && (
				<PersonListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					gruppeInfo={gruppe}
					identer={identer}
					bestillingerById={bestillingerById}
				/>
			)}
			{visning === VisningType.VISNING_BESTILLING && (
				<BestillingListeConnector
					iLaastGruppe={erLaast}
					brukertype={brukertype}
					bestillingerById={bestillingerById}
					gruppeInfo={gruppe}
				/>
			)}
		</div>
	)
}
