import React from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import { Accordion, CopyButton, FormSummary } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { useHendelseId } from '@/utils/hooks/useHendelseId'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export type RelatertPerson = {
	type: string
	id: string
}

type HendelseIdDataVisningProps = {
	ident: string
	relatertePersoner?: RelatertPerson[]
}

type Hendelse = Record<string, unknown>

type Ordre = {
	infoElement?: string
	hendelser?: Hendelse[]
}

type HendelseData = {
	ident?: string
	ordrer?: Ordre[]
	hovedperson?: {
		ordrer?: Ordre[]
	}
	relasjoner?: unknown[]
}

type HendelseIdInfoProps = {
	ident: string
	relatertIdent?: string
	onCategoryOpened?: () => void
}

const ApiFeilmelding = ({ feil }: { feil: string }) => <div role="alert">{feil}</div>

const formatValue = (value: unknown): React.ReactNode => {
	if (value === null || value === undefined) return '–'
	if (typeof value === 'object') return JSON.stringify(value)
	return String(value)
}

const formatSummaryValue = (key: string, value: unknown): React.ReactNode => {
	if (key !== 'hendelseId' || value === null || value === undefined) {
		return formatValue(value)
	}

	const hendelseId = String(value)

	return (
		<span className="hendelse-id-copy">
			<span>{hendelseId}</span>
			<CopyButton size="xsmall" copyText={hendelseId} title="Kopier hendelse-ID" />
		</span>
	)
}

const HendelseAnswers = ({ hendelser }: { hendelser: Hendelse[] }) => {
	if (hendelser.length === 0) {
		return (
			<FormSummary.Answer>
				<FormSummary.Label>Ingen hendelser registrert</FormSummary.Label>
			</FormSummary.Answer>
		)
	}

	return (
		<>
			{hendelser.map((hendelse, idx) => (
				<FormSummary.Answer key={`${hendelse.id ?? idx}-${idx}`}>
					<FormSummary.Label>
						{hendelse.id !== undefined ? `ID ${hendelse.id}` : `Hendelse ${idx + 1}`}
					</FormSummary.Label>
					<FormSummary.Value>
						<FormSummary.Answers>
							{Object.entries(hendelse)
								.filter(([key]) => key !== 'id')
								.map(([key, value]) => (
									<FormSummary.Answer key={key}>
										<FormSummary.Label>{key}</FormSummary.Label>
										<FormSummary.Value>{formatSummaryValue(key, value)}</FormSummary.Value>
									</FormSummary.Answer>
								))}
						</FormSummary.Answers>
					</FormSummary.Value>
				</FormSummary.Answer>
			))}
		</>
	)
}

const OrdreFormSummary = ({ ordre }: { ordre: Ordre }) => (
	<FormSummary>
		<FormSummary.Header>
			<FormSummary.Heading level="3">{ordre.infoElement ?? 'Ukjent type'}</FormSummary.Heading>
		</FormSummary.Header>
		<FormSummary.Answers>
			<HendelseAnswers hendelser={ordre.hendelser ?? []} />
		</FormSummary.Answers>
	</FormSummary>
)

const HendelseIdInfo = ({ ident, relatertIdent, onCategoryOpened }: HendelseIdInfoProps) => {
	const { data, loading, error } = useHendelseId(ident, relatertIdent)

	if (loading) {
		return <Loading label="Laster hendelse-IDer" />
	}
	if (error) {
		return <ApiFeilmelding feil={error?.message || String(error)} />
	}
	if (!data) {
		return null
	}

	const typed = data as HendelseData
	const ordrer = relatertIdent ? (typed.ordrer ?? []) : (typed.hovedperson?.ordrer ?? [])

	if (ordrer.length === 0) {
		return (
			<div className="boks">
				<FormSummary>
					<FormSummary.Header>
						<FormSummary.Heading level="3">Hendelse-ID</FormSummary.Heading>
					</FormSummary.Header>
					<FormSummary.Answers>
						<FormSummary.Answer>
							<FormSummary.Label>Ingen hendelser funnet</FormSummary.Label>
						</FormSummary.Answer>
					</FormSummary.Answers>
				</FormSummary>
			</div>
		)
	}

	const groupedOrdrer = ordrer.reduce<Record<string, Hendelse[]>>((accumulator, ordre) => {
		const key = ordre.infoElement ?? 'Ukjent type'
		accumulator[key] = [...(accumulator[key] ?? []), ...(ordre.hendelser ?? [])]
		return accumulator
	}, {})

	return (
		<div className="boks">
			<Accordion size="small">
				{Object.entries(groupedOrdrer)
					.sort(([left], [right]) => left.localeCompare(right, 'no'))
					.map(([infoElement, hendelser]) => (
						<Accordion.Item key={infoElement} onOpenChange={(open) => open && onCategoryOpened?.()}>
							<Accordion.Header>
								<span className="hendelse-kategori-header">
									<span>{codeToNorskLabel(infoElement)}</span>
									<span
										className="hendelse-kategori-antall"
										aria-label={`${hendelser.length} hendelser`}
									>
										{hendelser.length}
									</span>
								</span>
							</Accordion.Header>
							<Accordion.Content>
								<OrdreFormSummary ordre={{ infoElement, hendelser }} />
							</Accordion.Content>
						</Accordion.Item>
					))}
			</Accordion>
		</div>
	)
}

type HendelseTooltipProps = {
	triggerText: string
	ident: string
	relatertIdent?: string
}

const HendelseTooltip = ({ triggerText, ident, relatertIdent }: HendelseTooltipProps) => {
	const [isStickyOpen, setIsStickyOpen] = React.useState(false)
	const [isTooltipOpen, setIsTooltipOpen] = React.useState(false)

	const handleVisibleChange = (visible: boolean) => {
		setIsTooltipOpen(visible)
		if (isStickyOpen && !visible) {
			setIsStickyOpen(false)
		}
	}

	const handleCategoryOpened = () => {
		setIsStickyOpen(true)
		setIsTooltipOpen(true)
	}

	return (
		<DollyTooltip
			useExternalTooltip={true}
			content={
				<HendelseIdInfo
					ident={ident}
					relatertIdent={relatertIdent}
					onCategoryOpened={handleCategoryOpened}
				/>
			}
			externalTrigger={isStickyOpen ? ['click'] : ['hover']}
			externalPopupVisible={isTooltipOpen}
			externalOnVisibleChange={handleVisibleChange}
			align={{ offset: [0, -10] }}
			overlayStyle={{ opacity: 1 }}
			destroyTooltipOnHide={{ keepParent: false }}
		>
			<button type="button" className="miljoe-knapp">
				{triggerText}
			</button>
		</DollyTooltip>
	)
}

export const HendelseIdDataVisning = ({ ident, relatertePersoner }: HendelseIdDataVisningProps) => {
	if (!ident) {
		return null
	}

	return (
		<div className="flexbox--flex-wrap">
			<HendelseTooltip triggerText={ident} ident={ident} />
			{relatertePersoner?.map((person) => (
				<HendelseTooltip
					key={`${person.type}-${person.id}`}
					triggerText={`${person.id} (${person.type})`}
					ident={ident}
					relatertIdent={person.id}
				/>
			))}
		</div>
	)
}
