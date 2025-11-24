import { useAsync } from 'react-use'
import Loading from '@/components/ui/loading/Loading'
import { TpsDataVisning } from './TpsDataVisning'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { BankkontoApi, TpsMessagingApi } from '@/service/Api'
import { KontoregisterData } from '@/service/services/kontoregister/KontoregisterService'

type PersonMiljoeinfoProps = {
	bankIdBruker: boolean
	ident: string
	miljoe: string
}

export const PersonMiljoeinfo = ({ bankIdBruker, ident, miljoe }: PersonMiljoeinfoProps) => {
	const state = useAsync(async () => {
		if (ident) {
			return bankIdBruker
				? TpsMessagingApi.getTpsPersonInfo(ident, miljoe ? [miljoe] : ['q1'])
				: TpsMessagingApi.getTpsPersonInfoAllEnvs(ident)
		}
	}, [])

	const kontoregisterState = useAsync(async () => {
		if (ident) {
			return BankkontoApi.hentKonto(ident)
		}
	}, [])

	if (!ident) {
		return null
	}

	if (state.value && kontoregisterState.value?.data?.aktivKonto) {
		const kontoRegisterData: KontoregisterData = kontoregisterState.value.data

		//@ts-ignore
		state.value.data.map((m) => {
			if (m.person) {
				if (kontoRegisterData.aktivKonto.utenlandskKontoInfo) {
					m.person.bankkontonrUtland = {
						kontonummer: kontoRegisterData.aktivKonto.kontonummer,
						swift: kontoRegisterData.aktivKonto.utenlandskKontoInfo.swiftBicKode,
						landkode: kontoRegisterData.aktivKonto.utenlandskKontoInfo.bankLandkode,
						banknavn: kontoRegisterData.aktivKonto.utenlandskKontoInfo.banknavn,
						iban: kontoRegisterData.aktivKonto.kontonummer,
						valuta: kontoRegisterData.aktivKonto.utenlandskKontoInfo.valutakode,
						bankAdresse1: kontoRegisterData.aktivKonto.utenlandskKontoInfo.bankadresse1,
						bankAdresse2: kontoRegisterData.aktivKonto.utenlandskKontoInfo.bankadresse2,
						bankAdresse3: kontoRegisterData.aktivKonto.utenlandskKontoInfo.bankadresse3,
					}
				} else {
					m.person.bankkontonrNorsk = {
						kontonummer: kontoRegisterData.aktivKonto.kontonummer,
					}
				}
			}
		})
	}

	return (
		<div>
			<SubOverskrift label="Opprettet i miljøer" iconKind="vis-tps-data" />
			{state.loading && <Loading label="Laster miljøer" />}
			{/* @ts-ignore */}
			{state.value && <TpsDataVisning data={state.value.data} />}
			{state.value && (
				<p>
					<i>
						Hold pekeren over et miljø for å se dataene som finnes på denne personen i TPS for det
						aktuelle miljøet. <br />
						(Q1 og Q2 vil alltid motsvare respektive PDL-miljøer, og visning har nå blitt
						ekskludert.)
					</i>
				</p>
			)}
		</div>
	)
}
