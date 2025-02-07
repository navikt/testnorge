import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatDateTime, oversettBoolean } from '@/utils/DataFormatter'
import { useArbeidssoekerTyper } from '@/utils/hooks/useArbeidssoekerregisteret'

export const showTyperLabel = (type: string, value: string) => {
	const { data, loading, error } = useArbeidssoekerTyper(type)
	if (loading || error) {
		return value
	}
	return data?.find((item: any) => item?.value === value)?.label || value
}

export const ArbeidssoekerregisteretVisning = ({ data, loading }) => {
	if (loading) {
		return <Loading label="Laster arbeidssøkerregisteret-data" />
	}

	if (!data) {
		return null
	}

	const jobbsituasjonsdetaljer = data.jobbsituasjonsdetaljer

	return (
		<div>
			<SubOverskrift label="Arbeidssøkerregisteret" iconKind="cv" />
			<div className="person-visning_content">
				<TitleValue title="Utført av" value={showTyperLabel('BRUKERTYPE', data.utfoertAv)} />
				<TitleValue title="Kilde" value={data.kilde} />
				<TitleValue title="Årsak" value={data.aarsak} />
				<TitleValue title="Utdanningsnivå" value={showTyperLabel('NUSKODE', data.nuskode)} />
				<TitleValue
					title="Beskrivelse av jobbsituasjonen"
					value={showTyperLabel('JOBBSITUASJONSBESKRIVELSE', data.jobbsituasjonsbeskrivelse)}
				/>
				<TitleValue title="Utdanning bestått" value={oversettBoolean(data.utdanningBestaatt)} />
				<TitleValue title="Utdanning godkjent" value={oversettBoolean(data.utdanningGodkjent)} />
				<TitleValue
					title="Helse hindrer arbeid"
					value={oversettBoolean(data.helsetilstandHindrerArbeid)}
				/>
				<TitleValue
					title="Andre forhold hindrer arbeid"
					value={oversettBoolean(data.andreForholdHindrerArbeid)}
				/>
				<TitleValue
					title="Registreringstidspunkt"
					value={formatDateTime(data.registreringstidspunkt)}
				/>
				<div className="flexbox--full-width">
					<h3>Detaljer om jobbsituasjonen</h3>
					<div className="flexbox--flex-wrap">
						<TitleValue
							title="Gjelder fra dato"
							value={formatDate(jobbsituasjonsdetaljer?.gjelderFraDato)}
						/>
						<TitleValue
							title="Gjelder til dato"
							value={formatDate(jobbsituasjonsdetaljer?.gjelderTilDato)}
						/>
						<TitleValue title="Stilling" value={jobbsituasjonsdetaljer?.stillingstittel} />
						<TitleValue title="Stillingsprosent" value={jobbsituasjonsdetaljer?.stillingsprosent} />
						<TitleValue
							title="Siste dag med lønn"
							value={formatDate(jobbsituasjonsdetaljer?.sisteDagMedLoenn)}
						/>
						<TitleValue
							title="Siste arbeidsdag"
							value={formatDate(jobbsituasjonsdetaljer?.sisteArbeidsdag)}
						/>
					</div>
				</div>
			</div>
		</div>
	)
}
