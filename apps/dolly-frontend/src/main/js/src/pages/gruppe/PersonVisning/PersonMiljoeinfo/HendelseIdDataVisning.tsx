import React from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import { Accordion, Alert, CopyButton } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { useHendelseId } from '@/utils/hooks/useHendelseId'
import { codeToNorskLabel } from '@/utils/DataFormatter'
import { ApiFeilmelding } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import StyledAlert from '@/components/ui/alert/StyledAlert'

export type RelatertPerson = {
	type: string
	id: string
}

type HendelseIdDataVisningProps = {
	ident: string
	relatertePersoner?: RelatertPerson[]
	onHoverAvailabilityChange?: (tilgjengelig: boolean) => void
}

type Hendelse = {
	id?: number
	status?: string
	hendelseId?: string
	error?: string
}

type Ordre = {
	infoElement?: string
	hendelser?: Hendelse[]
}

type HendelseData = {
	ident?: string
	ordrer?: Ordre[]
	hovedperson?: {
		ident?: string
		ordrer?: Ordre[]
	}
	relasjoner?: unknown[]
}

type HendelseIdInfoProps = {
	ident: string
	relatertIdent?: string
	onCategoryOpened?: () => void
}

type RelatertHendelseElementProps = {
	ident: string
	person: RelatertPerson
	visTooltip: boolean
	visImportMelding?: boolean
	onImportertChange: (
		personId: string,
		tilstand: { erImportertRelasjon: boolean; erAvklart: boolean },
	) => void
}

const IMPORTERT_RELASJON_MESSAGE =
	'Hendelser utledes fra bestilling og ettersom denne relasjonen kun er importert, kan de ikke vises på denne identen. Gå til tilhørende hovedperson for å vise hendelser.'

const NOISE_INFOELEMENTER = new Set(['PDL_SLETTING', 'PDL_OPPRETT_PERSON'])

const hendelseHarFeil = (hendelse: Hendelse): boolean =>
	Boolean(hendelse.error) || (hendelse.status != null && hendelse.status !== 'OK')

const ImportertRelasjonMelding = () => {
	return (
		<StyledAlert variant="info" size="small" className="hendelse-importert-relasjon">
			{IMPORTERT_RELASJON_MESSAGE}
		</StyledAlert>
	)
}

const HendelseInnhold = ({ hendelse }: { hendelse: Hendelse }) => {
	if (hendelseHarFeil(hendelse)) {
		return (
			<div className="person-visning_content">
				<TitleValue title="Feil" size="full-width">
					<span className="hendelse-feil">{hendelse.error ?? hendelse.status}</span>
				</TitleValue>
			</div>
		)
	}

	if (hendelse.hendelseId) {
		return (
			<div className="person-visning_content">
				<TitleValue title="Hendelse-ID" size="full-width">
					<span className="hendelse-id-copy">
						<span className="hendelse-id-value">{hendelse.hendelseId}</span>
						<CopyButton size="xsmall" copyText={hendelse.hendelseId} title="Kopier hendelse-ID" />
					</span>
				</TitleValue>
			</div>
		)
	}

	return (
		<div className="person-visning_content">
			<TitleValue title="Status" value={hendelse.status} />
		</div>
	)
}

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
				<ImportertRelasjonMelding />
			</div>
		)
	}

	const groupedOrdrer = ordrer.reduce<Record<string, Hendelse[]>>((accumulator, ordre) => {
		const key = ordre.infoElement ?? 'Ukjent type'
		accumulator[key] = [...(accumulator[key] ?? []), ...(ordre.hendelser ?? [])]
		return accumulator
	}, {})

	const synligeOrdrer = Object.entries(groupedOrdrer).filter(
		([infoElement, hendelser]) =>
			!NOISE_INFOELEMENTER.has(infoElement) || hendelser.some(hendelseHarFeil),
	)

	if (synligeOrdrer.length === 0) {
		return (
			<div className="boks">
				<Alert variant="info" size="small">
					Ingen hendelser å vise.
				</Alert>
			</div>
		)
	}

	return (
		<div className="boks">
			<Accordion size="small">
				{synligeOrdrer
					.sort(([left], [right]) => left.localeCompare(right, 'no'))
					.map(([infoElement, hendelser]) => {
						const harFeil = hendelser.some(hendelseHarFeil)
						return (
							<Accordion.Item
								key={infoElement}
								className={harFeil ? 'hendelse-kategori-feil' : undefined}
								onOpenChange={(open) => open && onCategoryOpened?.()}
							>
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
									<DollyFieldArray data={hendelser} nested>
										{(hendelse: Hendelse) => <HendelseInnhold hendelse={hendelse} />}
									</DollyFieldArray>
								</Accordion.Content>
							</Accordion.Item>
						)
					})}
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
			<button type="button" className="miljoe-knapp hendelse-miljoe-knapp">
				{triggerText}
			</button>
		</DollyTooltip>
	)
}

const RelatertHendelseElement = ({
	ident,
	person,
	visTooltip,
	visImportMelding = true,
	onImportertChange,
}: RelatertHendelseElementProps) => {
	const { data, loading, error } = useHendelseId(ident, person.id)
	const typed = data as HendelseData | undefined
	const erImportertRelasjon =
		!loading && !error && Boolean(typed) && (typed?.ordrer?.length ?? 0) === 0
	React.useEffect(() => {
		onImportertChange(person.id, {
			erImportertRelasjon,
			erAvklart: !loading,
		})
	}, [person.id, erImportertRelasjon, loading, onImportertChange])

	if (erImportertRelasjon && visImportMelding) {
		return <ImportertRelasjonMelding />
	}
	if (!visTooltip) {
		return null
	}

	return (
		<HendelseTooltip
			triggerText={`${person.id} (${person.type})`}
			ident={ident}
			relatertIdent={person.id}
		/>
	)
}

export const HendelseIdDataVisning = ({
	ident,
	relatertePersoner,
	onHoverAvailabilityChange,
}: HendelseIdDataVisningProps) => {
	if (!ident) {
		return null
	}
	const unikeRelatertePersoner =
		relatertePersoner?.reduce<RelatertPerson[]>((acc, person) => {
			if (acc.some((existing) => existing.id === person.id)) {
				return acc
			}
			acc.push(person)
			return acc
		}, []) ?? []
	const harRelatertePersoner = unikeRelatertePersoner.length > 0
	const [relasjonTilstand, setRelasjonTilstand] = React.useState<
		Record<string, { erImportertRelasjon: boolean; erAvklart: boolean }>
	>({})
	const {
		data: hovedpersonData,
		loading: loadingHovedperson,
		error: errorHovedperson,
	} = useHendelseId(ident)
	const typedHovedpersonData = hovedpersonData as HendelseData | undefined
	const erImportertHovedperson =
		!loadingHovedperson &&
		!errorHovedperson &&
		Boolean(typedHovedpersonData?.hovedperson) &&
		(typedHovedpersonData?.hovedperson?.ordrer?.length ?? 0) === 0
	const harImportertRelasjon = unikeRelatertePersoner.some(
		(person) => relasjonTilstand[person.id]?.erImportertRelasjon,
	)
	const alleRelaterteAvklart = unikeRelatertePersoner.every(
		(person) => relasjonTilstand[person.id]?.erAvklart,
	)
	const skalViseKunImportvarsel = erImportertHovedperson || harImportertRelasjon
	const harTilgjengeligeHendelser =
		!loadingHovedperson && alleRelaterteAvklart && !skalViseKunImportvarsel
	const visHovedpersonTooltip = !skalViseKunImportvarsel && alleRelaterteAvklart
	const oppdaterImportertRelasjon = React.useCallback(
		(personId: string, tilstand: { erImportertRelasjon: boolean; erAvklart: boolean }) => {
			setRelasjonTilstand((previous) => {
				if (
					previous[personId]?.erImportertRelasjon === tilstand.erImportertRelasjon &&
					previous[personId]?.erAvklart === tilstand.erAvklart
				) {
					return previous
				}
				return {
					...previous,
					[personId]: tilstand,
				}
			})
		},
		[],
	)
	React.useEffect(() => {
		onHoverAvailabilityChange?.(harTilgjengeligeHendelser)
	}, [harTilgjengeligeHendelser, onHoverAvailabilityChange])

	return (
		<div className="flexbox--flex-wrap">
			{skalViseKunImportvarsel && <ImportertRelasjonMelding />}
			{visHovedpersonTooltip && (
				<HendelseTooltip
					triggerText={harRelatertePersoner ? `${ident} (HOVEDPERSON)` : ident}
					ident={ident}
				/>
			)}
			{unikeRelatertePersoner.map((person) => (
				<RelatertHendelseElement
					key={`${person.type}-${person.id}`}
					ident={ident}
					person={person}
					visTooltip={!skalViseKunImportvarsel}
					visImportMelding={false}
					onImportertChange={oppdaterImportertRelasjon}
				/>
			))}
		</div>
	)
}
