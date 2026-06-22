import React, { createContext, ReactNode, useContext, useMemo, useState } from 'react'
import { format, isValid, parseISO, subDays, subYears } from 'date-fns'
import {
	asNumber,
	fillMissingPersonDays,
	toPersonTrendData,
	withinDateRange,
} from './dashboardUtils'
import { createPersonTrendChartOptions } from './dashboardChartOptions'
import type { DashboardPerson } from '@/types/Dashboard'

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
		personerTotalt: number
		nye: number
		gjenopprettede: number
	}

	// Chart
	personTrendDataLength: number
	personTrendChartOptions: any
}

const DashboardPersonContext = createContext<DashboardPersonContextType | undefined>(undefined)

const QUICK_RANGE_OPTIONS = [
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
	{ value: 'all', label: 'All historikk' },
] as const

type QuickRangeValue = (typeof QUICK_RANGE_OPTIONS)[number]['value']

const isQuickRangeValue = (value: string): value is QuickRangeValue =>
	QUICK_RANGE_OPTIONS.some((option) => option.value === value)

interface DashboardPersonProviderProps {
	children: ReactNode
	dashboardPersoner: DashboardPerson[]
}

export const DashboardPersonProvider: React.FC<DashboardPersonProviderProps> = ({
	children,
	dashboardPersoner,
}) => {
	const [selectedQuickRange, setSelectedQuickRange] = useState<string | null>(null)
	const [fraDato, setFraDato] = useState('')
	const [tilDato, setTilDato] = useState('')

	// Initialize date range on first mount
	useMemo(() => {
		if (!fraDato && !tilDato && dashboardPersoner.length > 0) {
			const latestAvailableDateValue = dashboardPersoner
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
	}, [])

	const latestAvailableDateValue = useMemo(
		() =>
			dashboardPersoner
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(-1),
		[dashboardPersoner],
	)

	const latestAvailableDate = useMemo(
		() => (latestAvailableDateValue ? parseISO(latestAvailableDateValue) : undefined),
		[latestAvailableDateValue],
	)

	const quickRangeAnchorDate = useMemo(
		() => (latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()),
		[latestAvailableDate],
	)

	const earliestAvailableDateValue = useMemo(
		() =>
			dashboardPersoner
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(0),
		[dashboardPersoner],
	)

	const todayDate = format(new Date(), 'yyyy-MM-dd')

	const applyQuickRange = (quickRangeValue: string) => {
		const toDateValue = format(quickRangeAnchorDate, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'all'
				? (earliestAvailableDateValue ?? format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd'))
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
			dashboardPersoner
				.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
				.sort((a, b) => a.dato.localeCompare(b.dato)),
		[dashboardPersoner, fraDato, tilDato],
	)

	const completeFilteredPersoner = useMemo(
		() => fillMissingPersonDays(filteredPersoner, fraDato, tilDato),
		[filteredPersoner, fraDato, tilDato],
	)

	const summary = useMemo(
		() =>
			completeFilteredPersoner.reduce(
				(acc, personData) => {
					acc.personerTotalt += asNumber(personData.personerTotalt)
					acc.nye += asNumber(personData.nye)
					acc.gjenopprettede += asNumber(personData.gjenopprettede)
					return acc
				},
				{ personerTotalt: 0, nye: 0, gjenopprettede: 0 },
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
