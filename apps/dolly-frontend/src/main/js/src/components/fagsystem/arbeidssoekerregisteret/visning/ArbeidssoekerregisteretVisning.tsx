import { useState } from 'react'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { Logger } from '@/logger/Logger'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatDateTime, oversettBoolean } from '@/utils/DataFormatter'
import { useArbeidssoekerTyper } from '@/utils/hooks/useArbeidssoekerregisteret'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import type { ArbeidssoekerregisteretTypes } from '@/components/fagsystem/arbeidssoekerregisteret/arbeidssoekerregisteretTypes'
import { ArbeidssoekerregisteretStopp } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretStopp'

type ArbeidssoekerregisteretVisning = {
	data?: ArbeidssoekerregisteretTypes
	loading?: boolean
	ident: string
	onRefresh: () => Promise<unknown>
}

type TyperLabelProps = {
	type: string
	value?: string
	label: string
}

const StoppHandling = styled.div`
	display: flex;
	justify-content: flex-end;
	margin-top: -0.5rem;
	margin-bottom: 0.5rem;
`

export const TyperLabel = ({ type, value, label }: TyperLabelProps) => {
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
	ident,
	onRefresh,
}: ArbeidssoekerregisteretVisning) => {
	const [stoppetIdent, setStoppetIdent] = useState<string | null>(null)
	const visStoppetAlert = stoppetIdent === ident

	const onStopped = () => {
		setStoppetIdent(ident)
		void onRefresh().catch((error) => {
			Logger.error({
				event: 'Oppfriskning av arbeidssøkerregisteret etter stopp feilet',
				message: error instanceof Error ? error.message : 'Ukjent feil ved oppfriskning',
				uuid: window.uuid,
			})
		})
	}

	if (visStoppetAlert) {
		return (
			<div>
				<SubOverskrift label="Arbeidssøkerregisteret" iconKind="cv" />
				<StyledAlert variant={'info'} size={'small'}>
					Registrering som arbeidssøker er stoppet.
				</StyledAlert>
			</div>
		)
	}

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
			<StoppHandling>
				<ArbeidssoekerregisteretStopp ident={ident} onStopped={onStopped} />
			</StoppHandling>
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
