import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatDateTime, oversettBoolean } from '@/utils/DataFormatter'
import { useArbeidssoekerTyper } from '@/utils/hooks/useArbeidssoekerregisteret'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ArbeidssoekerregisteretTypes } from '@/components/fagsystem/arbeidssoekerregisteret/arbeidssoekerregisteretTypes'

type ArbeidssoekerregisteretVisning = {
	data?: ArbeidssoekerregisteretTypes
	loading?: boolean
}

export const TyperLabel = ({ type, value, label }) => {
	const { data: options, loading, error } = useArbeidssoekerTyper(type)
	if (loading || error || !options) {
		return <TitleValue title={label} value={value} />
	}
	const optionValue = options.find((option: any) => option.value === value)?.label ?? value
	return <TitleValue title={label} value={optionValue} />
}

export const ArbeidssoekerregisteretVisning = ({
	data,
	loading,
}: ArbeidssoekerregisteretVisning) => {
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
				<TyperLabel type={'BRUKERTYPE'} value={data.utfoertAv} label={'Utført av'} />
				<TitleValue title="Kilde" value={data.kilde} />
				<TitleValue title="Årsak" value={data.aarsak} />
				<TyperLabel type={'NUSKODE'} value={data.nuskode} label={'Utdanningsnivå'} />
				<TyperLabel
					type={'JOBBSITUASJONSBESKRIVELSE'}
					value={data.jobbsituasjonsbeskrivelse}
					label={'Beskrivelse av jobbsituasjonen'}
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
				{jobbsituasjonsdetaljer && !isEmpty(jobbsituasjonsdetaljer) && (
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
							<TitleValue
								title="Stillingsprosent"
								value={jobbsituasjonsdetaljer?.stillingsprosent}
							/>
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
				)}
			</div>
		</div>
	)
}
