import React, { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react'
import { format, isValid, parseISO, subDays, subYears } from 'date-fns'
import {
	asNumber,
	fillMissingPersonDays,
	isQuickRangeValue,
	monthsInRange,
	toPersonTrendData,
	withinDateRange,
} from './dashboardUtils'
import {
	createIdenterDonutChartOptions,
	createNyeGjenopprettedeDonutChartOptions,
	createPersonTrendChartOptions,
} from './dashboardChartOptions'
import {
	type DashboardBestillingerDTO,
	useDashboardBestillingerForMonths,
} from '@/utils/hooks/useDashboard'

interface DashboardPersonContextType {
	// Date range
	selectedQuickRange: string | null
	fraDato: string
	tilDato: string
	todayDate: string
	onFraDatoChange: (value: string) => void
	onTilDatoChange: (value: string) => void
	onQuickRangeClick: (quickRangeValue: string) => void

	// Summary
	filteredPersonerLength: number
	summary: {
		bestillinger: number
		personerTotalt: number
		nye: number
		gjenopprettede: number
		navIdenter: number
		testnorgeIdenter: number
	}

	// Chart
	personTrendDataLength: number
	personTrendChartOptions: any
	identerTotal: number
	identerDonutChartOptions: any
	nyeGjenopprettedeTotal: number
	nyeGjenopprettedeDonutChartOptions: any
}

const DashboardPersonContext = createContext<DashboardPersonContextType | undefined>(undefined)

const DATA_START_DATE = '2020-01-01'

interface DashboardPersonProviderProps {
	children: ReactNode
	dashboardBestillinger: DashboardBestillingerDTO[]
	mockModeEnabled?: boolean
}

export const DashboardPersonProvider: React.FC<DashboardPersonProviderProps> = ({
	children,
	dashboardBestillinger,
	mockModeEnabled = false,
}) => {
	const [selectedQuickRange, setSelectedQuickRange] = useState<string | null>(null)
	const [fraDato, setFraDato] = useState('')
	const [tilDato, setTilDato] = useState('')

	const requestedMonths = useMemo(
		() => (mockModeEnabled ? [] : monthsInRange(fraDato, tilDato)),
		[mockModeEnabled, fraDato, tilDato],
	)

	const { bestillingerForMonths } = useDashboardBestillingerForMonths(requestedMonths)

	const mergedBestillinger = useMemo(() => {
		const byDato = new Map<string, DashboardBestillingerDTO>()
		for (const personData of dashboardBestillinger) {
			if (personData.dato) {
				byDato.set(personData.dato, personData)
			}
		}
		for (const personData of bestillingerForMonths) {
			if (personData.dato) {
				byDato.set(personData.dato, personData)
			}
		}
		return Array.from(byDato.values())
	}, [dashboardBestillinger, bestillingerForMonths])

	// Initialize date range once data is available
	useEffect(() => {
		if (!fraDato && !tilDato && dashboardBestillinger.length > 0) {
			const latestAvailableDateValue = dashboardBestillinger
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(-1)

			const latestAvailableDate = latestAvailableDateValue
				? parseISO(latestAvailableDateValue)
				: undefined

			const quickRangeAnchorDate =
				latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()

			const todayValue = format(new Date(), 'yyyy-MM-dd')
			const fromValue = format(subDays(quickRangeAnchorDate, 29), 'yyyy-MM-dd')

			setFraDato(fromValue)
			setTilDato(todayValue)
			setSelectedQuickRange('month')
		}
	}, [dashboardBestillinger, fraDato, tilDato])

	const latestAvailableDateValue = useMemo(
		() =>
			dashboardBestillinger
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(-1),
		[dashboardBestillinger],
	)

	const latestAvailableDate = useMemo(
		() => (latestAvailableDateValue ? parseISO(latestAvailableDateValue) : undefined),
		[latestAvailableDateValue],
	)

	const quickRangeAnchorDate = useMemo(
		() => (latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()),
		[latestAvailableDate],
	)

	const todayDate = format(new Date(), 'yyyy-MM-dd')

	const applyQuickRange = (quickRangeValue: string) => {
		const toDateValue = format(quickRangeAnchorDate, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'all'
				? DATA_START_DATE
				: quickRangeValue === 'week'
					? format(subDays(quickRangeAnchorDate, 6), 'yyyy-MM-dd')
					: quickRangeValue === 'month'
						? format(subDays(quickRangeAnchorDate, 29), 'yyyy-MM-dd')
						: quickRangeValue === 'threeMonths'
							? format(subDays(quickRangeAnchorDate, 89), 'yyyy-MM-dd')
							: quickRangeValue === 'sixMonths'
								? format(subDays(quickRangeAnchorDate, 179), 'yyyy-MM-dd')
								: format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd')
		setFraDato(fromDateValue)
		setTilDato(toDateValue)
		if (isQuickRangeValue(quickRangeValue)) {
			setSelectedQuickRange(quickRangeValue)
		}
	}

	// Filter and aggregate person data
	const filteredPersoner = useMemo(
		() =>
			mergedBestillinger
				.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
				.sort((a, b) => a.dato.localeCompare(b.dato)),
		[mergedBestillinger, fraDato, tilDato],
	)

	const completeFilteredPersoner = useMemo(
		() => fillMissingPersonDays(filteredPersoner, fraDato, tilDato),
		[filteredPersoner, fraDato, tilDato],
	)

	const summary = useMemo(
		() =>
			completeFilteredPersoner.reduce(
				(acc, personData) => {
					acc.bestillinger += asNumber(personData.bestillinger)
					acc.personerTotalt += asNumber(personData.personerTotalt)
					acc.nye += asNumber(personData.nye)
					acc.gjenopprettede += asNumber(personData.gjenopprettede)
					acc.navIdenter += asNumber(personData.navIdenter)
					acc.testnorgeIdenter += asNumber(personData.testnorgeIdenter)
					return acc
				},
				{
					bestillinger: 0,
					personerTotalt: 0,
					nye: 0,
					gjenopprettede: 0,
					navIdenter: 0,
					testnorgeIdenter: 0,
				},
			),
		[completeFilteredPersoner],
	)

	const personTrendData = useMemo(
		() => toPersonTrendData(completeFilteredPersoner),
		[completeFilteredPersoner],
	)

	const personTrendChartOptions = useMemo(
		() => createPersonTrendChartOptions(personTrendData, {}),
		[personTrendData],
	)

	const identerDonutChartOptions = useMemo(
		() =>
			createIdenterDonutChartOptions({
				navIdenter: summary.navIdenter,
				testnorgeIdenter: summary.testnorgeIdenter,
			}),
		[summary.navIdenter, summary.testnorgeIdenter],
	)

	const nyeGjenopprettedeDonutChartOptions = useMemo(
		() =>
			createNyeGjenopprettedeDonutChartOptions({
				nye: summary.nye,
				gjenopprettede: summary.gjenopprettede,
			}),
		[summary.nye, summary.gjenopprettede],
	)

	const value: DashboardPersonContextType = {
		// Date range
		selectedQuickRange,
		fraDato,
		tilDato,
		todayDate,
		onFraDatoChange: (value: string) => {
			setFraDato(value)
			setSelectedQuickRange(null)
		},
		onTilDatoChange: (value: string) => {
			setTilDato(value)
			setSelectedQuickRange(null)
		},
		onQuickRangeClick: applyQuickRange,

		// Summary
		filteredPersonerLength: completeFilteredPersoner.length,
		summary,

		// Chart
		personTrendDataLength: personTrendData.length,
		personTrendChartOptions,
		identerTotal: summary.navIdenter + summary.testnorgeIdenter,
		identerDonutChartOptions,
		nyeGjenopprettedeTotal: summary.nye + summary.gjenopprettede,
		nyeGjenopprettedeDonutChartOptions,
	}

	return <DashboardPersonContext.Provider value={value}>{children}</DashboardPersonContext.Provider>
}

export const useDashboardPerson = () => {
	const context = useContext(DashboardPersonContext)
	if (!context) {
		throw new Error('useDashboardPerson must be used within DashboardPersonProvider')
	}
	return context
}
