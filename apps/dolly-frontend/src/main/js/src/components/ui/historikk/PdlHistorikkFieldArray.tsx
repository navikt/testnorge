import { ReactNode } from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import Panel from '@/components/ui/panel/Panel'
import './historikk.less'

export type PdlOpplysning = {
	hendelseId?: string
	metadata?: {
		historisk?: boolean
		opplysningsId?: string
	}
}

type IndeksertOpplysning<T> = {
	element: T
	idx: number
}

interface PdlHistorikkFieldArrayProps<T extends PdlOpplysning> {
	data: Array<T>
	pdlData?: Array<PdlOpplysning>
	children: (element: T, idx: number) => ReactNode
}

export const erHistoriskOpplysning = (
	opplysning: PdlOpplysning,
	pdlData?: Array<PdlOpplysning>,
): boolean => {
	if (typeof opplysning?.metadata?.historisk === 'boolean') {
		return opplysning.metadata.historisk
	}
	if (!opplysning?.hendelseId) {
		return false
	}
	const pdlOpplysning = pdlData?.find(
		(element) => element?.metadata?.opplysningsId === opplysning.hendelseId,
	)
	return pdlOpplysning?.metadata?.historisk === true
}

export const PdlHistorikkFieldArray = <T extends PdlOpplysning>({
	data,
	pdlData,
	children,
}: PdlHistorikkFieldArrayProps<T>) => {
	if (!data || data.length === 0) {
		return null
	}

	const indeksertData: Array<IndeksertOpplysning<T>> = data.map((element, idx) => ({
		element,
		idx,
	}))
	const gjeldende = indeksertData.filter(
		(opplysning) => !erHistoriskOpplysning(opplysning.element, pdlData),
	)
	const historiske = indeksertData.filter((opplysning) =>
		erHistoriskOpplysning(opplysning.element, pdlData),
	)

	const renderOpplysning = (opplysning: IndeksertOpplysning<T>) =>
		children(opplysning.element, opplysning.idx)

	if (historiske.length === 0) {
		return (
			<DollyFieldArray data={gjeldende} header="" nested>
				{renderOpplysning}
			</DollyFieldArray>
		)
	}

	return (
		<div className="array-historikk">
			<DollyFieldArray data={gjeldende} header="" nested>
				{renderOpplysning}
			</DollyFieldArray>
			<Panel heading="Historikk">
				<DollyFieldArray data={historiske} nested>
					{renderOpplysning}
				</DollyFieldArray>
			</Panel>
		</div>
	)
}
